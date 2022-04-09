package com.cgc.controller;

import com.cgc.annotation.LoginRequired;
import com.cgc.entity.User;
import com.cgc.service.FollowService;
import com.cgc.service.LikeService;
import com.cgc.service.UserService;
import com.cgc.util.CommunityConstant;
import com.cgc.util.CommunityUtil;
import com.cgc.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    /**
     * 访问账号设置页面
     *
     * @return 模板文件
     */
    @LoginRequired
    @RequestMapping("/setting")
    public String toSettingPage() {
        return "/site/setting";
    }

    /**
     * 账号设置界面的上传头像功能
     *
     * @param headerImage 该参数用于接收前端传回的头像文件
     * @param model       用于向前端返回数据
     * @return 上传成功重定向回到首页，上传失败返回到账号设置页面
     */
    //参数中的MultiPartFile是spring框架提供的，用于接收html中input type=file上传的文件
    @LoginRequired
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {

        if (headerImage == null) {
            model.addAttribute("error", "上传的图片为空！");
            return "/site/setting";
        }
        //1. 如果图片不为空，说明后台已经拿到了用户上传的图片，下一步就是把图片存到服务器中的某个地方，然后更新用户的头像地址
        //  1.1 将图片存储到服务器磁盘（为了防止不同用户上传的图片名字相同，被覆盖，所以存储时都采用随机生成的文件名）
        //      1.1.1 获取源文件的格式，例如.png,.jpg
        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf('.'));
        //      1.1.2 生成随机文件名
        fileName = CommunityUtil.genUUID() + suffix;
        //      1.1.3 完整的目标路径
        File desPath = new File(uploadPath + "/" + fileName);
        //      1.1.4 调用MultiPartFile类提供的写入文件的方法tranferTo
        try {
            headerImage.transferTo(desPath);
        } catch (IOException e) {
            throw new RuntimeException("上传文件失败" + e);
        }

        //  1.2 更新用户头像地址
        //      1.2.1 拼接用户当前头像的URL(设计：http://localhost:8080/user/header/xxx.png)
        String headerUrl = domain + "/" + "user/header/" + fileName;
        userService.updateHeaderUrl(hostHolder.getUser().getId(), headerUrl);

        return "redirect:/index";
    }

    /**
     * 从服务器的磁盘文件中获取用户头像文件
     *
     * @param fileName 头像文件名：由前端提供
     * @param response response可以向前端发送各种数据，在这里用于输出图片文件到前端
     */
    @RequestMapping("/header/{fileName}")
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        //文件存储路径
        fileName = uploadPath + "/" + fileName;
        //文件类型
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        //告诉前端返回值的类型
        response.setContentType("image/" + suffix);
        try (OutputStream os = response.getOutputStream(); FileInputStream fis = new FileInputStream(fileName)) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 个人主页（注意可能是登录者自己的主页，也可能是查看其他用户的个人主页）
     * 所以这里的user对象不能通过hostholder获取，那样这个方法就只能获取当前用户的个人主页，想查看其它用户的个人主页还要另外编写方法
     *
     * @param userId 要查看哪个用户主页，它的userid
     * @param model  用于把查询到的该用户的信息发送给前端(信息包括用户的基本信息user对象，用户的获赞信息（redis中获取），用户的粉丝信息（redis中获取））
     * @return 模板引擎文件
     */
    @RequestMapping("/profile/{userId}")
    public String getProfilePage(@PathVariable int userId, Model model) {
        User loginUser = hostHolder.getUser();
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("用于不存在");
        }

        model.addAttribute("user", user);
        model.addAttribute("loginUser", loginUser);
        model.addAttribute("likeCount", likeService.findUserLikeCount(userId));
        model.addAttribute("fansCount", followService.findFansCount(ENTITY_TYPE_USER, userId));
        model.addAttribute("followCount", followService.findFollowCount(userId, ENTITY_TYPE_USER));
        model.addAttribute("isFollowing", followService.isFollowing(loginUser == null ? null : loginUser.getId(), ENTITY_TYPE_USER, userId));

        return "/site/profile";
    }
}
