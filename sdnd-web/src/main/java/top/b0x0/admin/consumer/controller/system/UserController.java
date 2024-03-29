package top.b0x0.admin.consumer.controller.system;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.b0x0.admin.common.dto.RoleSmallDto;
import top.b0x0.admin.common.dto.UserDto;
import top.b0x0.admin.common.exception.BusinessErrorException;
import top.b0x0.admin.common.mapstruct.UserMapper;
import top.b0x0.admin.common.pojo.system.SysUser;
import top.b0x0.admin.common.util.RsaUtils;
import top.b0x0.admin.common.util.StringUtils;
import top.b0x0.admin.common.util.constants.CommonConstants;
import top.b0x0.admin.common.util.enums.CodeEnum;
import top.b0x0.admin.common.util.properties.RsaProperties;
import top.b0x0.admin.common.vo.R;
import top.b0x0.admin.common.vo.req.UserPassReqVo;
import top.b0x0.admin.common.vo.req.UserQueryReq;
import top.b0x0.admin.consumer.annotaion.Log;
import top.b0x0.admin.consumer.shiro.ShiroUtils;
import top.b0x0.admin.service.module.system.RoleService;
import top.b0x0.admin.service.module.system.UserService;
import top.b0x0.admin.service.module.system.VerifyService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @author TANG
 * @date 2020-11-23
 */
@Api(tags = "系统：用户管理")
@RestController
@RequestMapping("system/user")
public class UserController {

    @Reference(version = "${service.version}", check = false)
    UserService userService;
    @Reference(version = "${service.version}", check = false)
    RoleService roleService;
    @Reference(version = "${service.version}", check = false)
    VerifyService verificationCodeService;

    UserMapper userMapper;

//    @ApiOperation("查询用户")
//    @GetMapping
////    @RequiresPermissions("@el.check('SysUser:list')")
//    public R query(UserQueryReq userQueryReq) {
//        return R.ok(userService.selectList(userQueryReq));
//    }
//
//    @ApiOperation("导出用户数据")
//    @GetMapping(value = "/download")
//    // @RequiresPermissions("@el.check('SysUser:list')")
//    public void download(HttpServletResponse response, UserQueryReq userQueryReq) throws IOException {
//        List<SysUser> records = userService.selectList(userQueryReq).getList();
//        List<UserDto> userDtoList = userMapper.toDto(records);
//        userService.download(userDtoList, response);
//    }

    @Log(description = "新增用户")
    @ApiOperation("新增用户")
    @PostMapping("/add")
    // @RequiresPermissions("@el.check('SysUser:add')")
    public R create(@Validated @RequestBody SysUser user) {
        checkLevel(user);
        String generateSalt = ShiroUtils.generateSalt();
        if (StringUtils.isBlank(user.getPassword())) {
            // 默认密码 123456
            user.setPassword(ShiroUtils.sha256Pass(CommonConstants.DEFAULT_PASSWORD, generateSalt));
        } else {
            user.setPassword(ShiroUtils.sha256Pass(user.getPassword(), generateSalt));
        }
        user.setSalt(generateSalt);
        userService.create(user, ShiroUtils.getUserEntity());
        return R.ok();
    }

    @Log(description = "修改用户")
    @ApiOperation("修改用户")
    @PutMapping("/update")
    // @RequiresPermissions("@el.check('SysUser:edit')")
    public R update(@Validated @RequestBody SysUser resources) {
//        checkLevel(resources);
        userService.update(resources);
        return R.ok();
    }

    @Log(description = "修改用户：个人中心")
    @ApiOperation("修改用户：个人中心")
    @PutMapping(value = "center")
    public R center(@Validated @RequestBody SysUser resources) {
        Long userId = ShiroUtils.getUserId();
        if (!resources.getUserId().equals(userId)) {
            throw new BusinessErrorException("不能修改他人资料");
        }
        userService.updateCenter(resources);
        return R.ok();
    }

    @Log(description = "删除用户")
    @ApiOperation("删除用户")
    @DeleteMapping("/del")
    // @RequiresPermissions("@el.check('SysUser:del')")
    public R delete(@RequestBody Set<Long> ids) {
        Integer currentUserId = 1;
        List<Integer> integerList = new ArrayList<>();
        integerList.add(currentUserId);
        for (Long id : ids) {
            // roleService.findByUsersId(SecurityUtils.getCurrentUserId()).stream().map(RoleSmallDto::getLevel).collect(Collectors.toList())
            Integer currentLevel = Collections.min(integerList);
            Integer optLevel = Collections.min(roleService.findByUsersId(id).stream().map(RoleSmallDto::getLevel).collect(Collectors.toList()));
            if (currentLevel > optLevel) {
                throw new BusinessErrorException("角色权限不足，不能删除：" + userService.findById(id).getUsername());
            }
        }
        userService.delete(ids);
        return R.ok();
    }

    @ApiOperation("修改密码")
    @PostMapping(value = "/updatePass")
    public R updatePass(@RequestBody UserPassReqVo passVo) throws Exception {
        String oldPass = RsaUtils.decryptByPrivateKey(RsaProperties.privateKey, passVo.getOldPass());
        String newPass = RsaUtils.decryptByPrivateKey(RsaProperties.privateKey, passVo.getNewPass());
        // SecurityUtils.getCurrentUsername()
/*        SysUser SysUser = userService.findByName("currentUsername");
        if (!passwordEncoder.matches(oldPass, SysUser.getPassword())) {
            throw new BusinessErrorException("修改失败，旧密码错误");
        }
        if (passwordEncoder.matches(newPass, SysUser.getPassword())) {
            throw new BusinessErrorException("新密码不能与旧密码相同");
        }
        userService.updatePass(SysUser.getUsername(), passwordEncoder.encode(newPass));*/
        return R.ok();
    }

//    @ApiOperation("修改头像")
//    @PostMapping(value = "/updateAvatar")
//    public R updateAvatar(@RequestParam MultipartFile avatar) {
//        return R.ok(userService.updateAvatar(avatar));
//    }

    @Log(description = "修改邮箱")
    @ApiOperation("修改邮箱")
    @PostMapping(value = "/updateEmail/{code}")
    public R updateEmail(@PathVariable String code, @RequestBody SysUser SysUser) throws Exception {
        String password = RsaUtils.decryptByPrivateKey(RsaProperties.privateKey, SysUser.getPassword());
        // String currentUsername = SecurityUtils.getCurrentUsername()
        SysUser userDto = userService.findByName("currentUsername");
/*        if (!passwordEncoder.matches(password, userDto.getPassword())) {
            throw new BusinessErrorException("密码错误");
        }*/
        verificationCodeService.validated(CodeEnum.EMAIL_RESET_EMAIL_CODE.getKey() + SysUser.getEmail(), code);
        userService.updateEmail(userDto.getUsername(), SysUser.getEmail());
        return R.ok();
    }

    /**
     * 如果当前用户的角色级别低于创建用户的角色级别，则抛出权限不足的错误
     *
     * @param resources /
     */
    private void checkLevel(SysUser resources) {
        Integer currentLevel = Collections.min(roleService.findByUsersId(ShiroUtils.getUserId()).stream().map(RoleSmallDto::getLevel).collect(Collectors.toList()));
        Integer optLevel = roleService.findByRoles(resources.getRoles());
        if (currentLevel > optLevel) {
            throw new BusinessErrorException("角色权限不足");
        }
    }
}
