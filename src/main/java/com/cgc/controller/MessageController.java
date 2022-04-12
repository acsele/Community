package com.cgc.controller;

import com.alibaba.fastjson.JSONObject;
import com.cgc.entity.Message;
import com.cgc.entity.Page;
import com.cgc.entity.User;
import com.cgc.service.DiscussPostService;
import com.cgc.service.MessageService;
import com.cgc.service.UserService;
import com.cgc.util.CommunityConstant;
import com.cgc.util.CommunityUtil;
import com.cgc.util.HostHolder;
import jdk.nashorn.internal.scripts.JO;
import org.ietf.jgss.Oid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.*;

@Controller
public class MessageController implements CommunityConstant {

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private DiscussPostService discussPostService;

    @RequestMapping("/letter/list")
    public String showConversationList(Model model, Page page) {
        User user = hostHolder.getUser();

        //设置分页
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));
        page.setLimit(5);

        //展示消息列表（每一项包括的信息有：发消息的用户信息，最新的一条消息文本）
        List<Map<String, Object>> latestMessages = new ArrayList<>();

        List<Message> mList = messageService.findLatestMessages(user.getId(), page.getLimit(), page.getOffset());
        for (Message message : mList) {
            HashMap<String, Object> latestMessage = new HashMap<>();
            int unReadCount = messageService.findUnreadMessagesCount(user.getId(), message.getConversationId());

            //每一项消息中包含的信息
            latestMessage.put("message", message);
            latestMessage.put("unReadCount", unReadCount);
            //我有可能是发送者也有可能是接收者，但是消息列表中展示的应该是和我聊天的人的名字，始终都不能是我的名字
            User fromUser = userService.findUserById(message.getFromId());
            latestMessage.put("user", user.getId() == fromUser.getId() ? userService.findUserById(message.getToId()) : fromUser);

            latestMessages.add(latestMessage);
        }
        model.addAttribute("latestMessages", latestMessages);
        model.addAttribute("unReadCountSum", messageService.findUnreadMessagesCount(user.getId(), null));
        model.addAttribute("noticeUnreadCount", messageService.findUnReadNoticeCount(user.getId(), null));
        model.addAttribute("messageCount", messageService.findUnReadNoticeCount(user.getId(), null)
                + messageService.findUnreadMessagesCount(user.getId(), null));

        return "/site/letter";
    }

    @RequestMapping("/letter/detail/{conversationId}")
    public String messageDetail(@PathVariable String conversationId, Model model, Page page) {

        User user = hostHolder.getUser();


        //设置分页
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.findMessagesCount(conversationId));
        page.setLimit(5);

        //展示消息列表（每一项包括的信息有：发消息的用户信息，最新的一条消息文本）
        List<Map<String, Object>> messages = new ArrayList<>();

        List<Message> mList = messageService.findMessages(conversationId, page.getLimit(), page.getOffset());
        if (mList != null) {
            for (Message msg : mList) {
                HashMap<String, Object> message = new HashMap<>();
                //每一项消息中包含的信息
                message.put("message", msg);
                message.put("user", userService.findUserById(msg.getFromId()));

                messages.add(message);
            }
            User fromUser = userService.findUserById(mList.get(0).getFromId());
            model.addAttribute("targetUser", user.getId() == fromUser.getId() ? userService.findUserById(mList.get(0).getToId()) : fromUser);
        }
        model.addAttribute("messages", messages);

        //把消息置为已读
        messageService.readMessage(mList);

        return "/site/letter-detail";
    }


    @RequestMapping(value = "/letter/send", method = RequestMethod.POST)
    @ResponseBody
    public String sendMessage(String toName, String content) {

        if (userService.findUserByName(toName) == null) {
            return CommunityUtil.genJson(1, "用户名不存在！");
        }
        int toUserId = userService.findUserByName(toName).getId();
        int fromUserId = hostHolder.getUser().getId();

        Message message = new Message();
        message.setFromId(fromUserId);
        message.setToId(toUserId);
        message.setContent(content);
        //保证在拼接会话id时，始终是小的id在前
        message.setConversationId(toUserId < fromUserId ? toUserId + "_" + fromUserId : fromUserId + "_" + toUserId);
        message.setCreateTime(new Date());
        message.setStatus(0);

        messageService.addMessage(message);

        return CommunityUtil.genJson(0, "发送成功！");
    }


    // 系统通知相关

    public Map<String, Object> findNoticeContent(User user, String topic) {
        Map<String, Object> noticeItem = new HashMap<>();
        Message latestNotice = messageService.findLatestNotice(user.getId(), topic);

        if (latestNotice != null) {
            Map<String, Object> noticeContent = JSONObject.parseObject(latestNotice.getContent());

            noticeItem.put("fromUser", userService.findUserById((Integer) noticeContent.get("userId")));
            noticeItem.put("entityType", noticeContent.get("entityType"));
            noticeItem.put("entityId", noticeContent.get("entityId"));
            noticeItem.put("post", noticeContent.get("postId"));
            noticeItem.put("time", latestNotice.getCreateTime());
            noticeItem.put("count", messageService.findNoticeCount(user.getId(), topic));
            noticeItem.put("unRead", messageService.findUnReadNoticeCount(user.getId(), topic));
            return noticeItem;
        }
        return null;

    }

    @RequestMapping("/notice/list")
    public String getNoticeListPage(Model model) {
        User user = hostHolder.getUser();

        model.addAttribute("commentNoticeItem", findNoticeContent(user, TOPIC_COMMENT));
        model.addAttribute("likeNoticeItem", findNoticeContent(user, TOPIC_Like));
        model.addAttribute("followNoticeItem", findNoticeContent(user, TOPIC_FOLLOW));

        model.addAttribute("letterUnreadCount", messageService.findUnreadMessagesCount(user.getId(), null));
        model.addAttribute("noticeUnreadCount", messageService.findUnReadNoticeCount(user.getId(), null));
        model.addAttribute("messageCount", messageService.findUnReadNoticeCount(user.getId(), null)
                + messageService.findUnreadMessagesCount(user.getId(), null));


        return "/site/notice";
    }

    @RequestMapping("/notice/detail/{topic}")
    public String noticeDetails(@PathVariable String topic, Model model, Page page) {
        User user = hostHolder.getUser();

        //设置分页
        page.setPath("/notice/detail/" + topic);
        page.setLimit(5);
        page.setRows(messageService.findNoticeCount(user.getId(), topic));

        List<Message> noticeList = messageService.findNoticeList(user.getId(), topic, page.getLimit(), page.getOffset());
        List<Map<String, Object>> notices = new ArrayList<>();

        for (Message message : noticeList) {
            Map<String, Object> notice = new HashMap<>();
            Map<String, Object> content = JSONObject.parseObject(message.getContent(), HashMap.class);

            notice.put("noticeBaseInfo",message);
            notice.put("fromUser",userService.findUserById(message.getFromId()));
            notice.put("user", userService.findUserById((Integer) content.get("userId")));
            notice.put("entityType", content.get("entityType"));
            notice.put("entityId", content.get("entityId"));
            notice.put("postId", content.get("postId"));
            notices.add(notice);
        }

        model.addAttribute("notices", notices);

        //设置已读
        messageService.readMessage(noticeList);

        return "/site/notice-detail";
    }


}
