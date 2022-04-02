package com.cgc.controller;

import com.cgc.entity.Message;
import com.cgc.entity.Page;
import com.cgc.entity.User;
import com.cgc.service.MessageService;
import com.cgc.service.UserService;
import com.cgc.util.CommunityUtil;
import com.cgc.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/letter")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @RequestMapping("/list")
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

        return "/site/letter";
    }

    @RequestMapping("/detail/{conversationId}")
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


    @RequestMapping(value = "/send", method = RequestMethod.POST)
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

}
