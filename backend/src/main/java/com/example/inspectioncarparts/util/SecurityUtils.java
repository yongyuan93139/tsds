package com.example.inspectioncarparts.util;

import com.example.inspectioncarparts.domain.entity.SysUser;
import com.example.inspectioncarparts.exception.AuthenticationException;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Spring Security工具类
 * 用于获取当前登录用户信息
 */
public class SecurityUtils {

    /**
     * 获取当前登录用户的Authentication对象
     *
     * @return Authentication对象，如果未登录则返回null
     */
    public static Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return authentication;
    }

    /**
     * 判断当前用户是否已登录
     *
     * @return 如果已登录返回true，否则返回false
     */
    public static boolean isAuthenticated() {
        return getAuthentication() != null;
    }

    /**
     * 获取当前登录用户的用户名
     *
     * @return 用户名，如果未登录则返回null
     */
    public static String getUsername() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }

        if (principal instanceof String) {
            return (String) principal;
        }

        return null;
    }

    /**
     * 获取当前登录用户的用户名，如果未登录则抛出异常
     *
     * @return 用户名
     * @throws AuthenticationException 如果未登录则抛出此异常
     */
    public static String getRequiredUsername() {
        String username = getUsername();
        if (username == null) {
            throw new AuthenticationException("用户未登录");
        }
        return username;
    }

    /**
     * 获取当前登录用户的用户ID
     *
     * @return 用户ID，如果未登录或无法获取则返回null
     */
    public static Integer getUserId() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            return null;
        }

        // 从LoginUser中获取用户ID
        Object principal = authentication.getPrincipal();
        if (principal instanceof SysUser) {
            return ((SysUser) principal).getId();
        }

        // 从JWT令牌中获取用户ID
        if (principal instanceof Claims) {
            Claims claims = (Claims) principal;
            return claims.get("userId", Integer.class);
        }

        // 如果使用自定义的UserDetails实现，可以从中获取用户ID
        if (principal instanceof UserDetails && principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getUserId();
        }

        return null;
    }

    /**
     * 获取当前登录用户的用户ID，如果未登录则抛出异常
     *
     * @return 用户ID
     * @throws AuthenticationException 如果未登录则抛出此异常
     */
    public static Integer getRequiredUserId() {
        Integer userId = getUserId();
        if (userId == null) {
            throw new AuthenticationException("用户未登录或无法获取用户ID");
        }
        return userId;
    }

    /**
     * 获取当前登录用户的角色列表
     *
     * @return 角色列表，如果未登录则返回空列表
     */
    public static List<String> getRoles() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            return Collections.emptyList();
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (authorities == null || authorities.isEmpty()) {
            return Collections.emptyList();
        }

        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    /**
     * 判断当前用户是否具有指定角色
     *
     * @param role 角色名称
     * @return 如果具有该角色返回true，否则返回false
     */
    public static boolean hasRole(String role) {
        List<String> roles = getRoles();
        return roles.contains(role) || roles.contains("ROLE_" + role);
    }

    /**
     * 判断当前用户是否具有指定角色，如果没有则抛出异常
     *
     * @param role 角色名称
     * @throws AuthenticationException 如果用户未登录或没有指定角色则抛出此异常
     */
    public static void checkRole(String role) {
        if (!isAuthenticated()) {
            throw new AuthenticationException("用户未登录");
        }
        if (!hasRole(role)) {
            throw new AuthenticationException("用户没有[" + role + "]角色权限", 403);
        }
    }

    /**
     * 判断当前用户是否具有指定权限
     *
     * @param permission 权限标识
     * @return 如果具有该权限返回true，否则返回false
     */
    public static boolean hasPermission(String permission) {
        List<String> permissions = getRoles(); // 在实际应用中，可能需要从角色映射到权限
        return permissions.contains(permission);
    }

    /**
     * 判断当前用户是否具有指定权限，如果没有则抛出异常
     *
     * @param permission 权限标识
     * @throws AuthenticationException 如果用户未登录或没有指定权限则抛出此异常
     */
    public static void checkPermission(String permission) {
        if (!isAuthenticated()) {
            throw new AuthenticationException("用户未登录");
        }
        if (!hasPermission(permission)) {
            throw new AuthenticationException("用户没有[" + permission + "]权限", 403);
        }
    }

    /**
     * 获取当前登录用户的LoginUser对象
     *
     * @return LoginUser对象，如果未登录或类型不匹配则返回null
     */
    public static SysUser getLoginUser() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof SysUser) {
            return (SysUser) principal;
        }

        return null;
    }

    /**
     * 获取当前登录用户的LoginUser对象，如果未登录则抛出异常
     *
     * @return LoginUser对象
     * @throws AuthenticationException 如果未登录或类型不匹配则抛出此异常
     */
    public static SysUser getRequiredLoginUser() {
        SysUser SysUser = getLoginUser();
        if (SysUser == null) {
            throw new AuthenticationException("用户未登录或会话已过期");
        }
        return SysUser;
    }

    /**
     * 获取当前登录用户的详细信息
     *
     * @return 用户详细信息，如果未登录则返回null
     */
    public static Object getUserDetails() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            return null;
        }

        return authentication.getPrincipal();
    }

    /**
     * 自定义UserDetails接口，用于获取用户ID
     * 项目中的UserDetails实现类应该实现此接口
     */
    public interface CustomUserDetails {
        Integer getUserId();
    }

    //查询用户真实姓名
    public static String getRealName(){
        SysUser SysUser = getLoginUser();
        if(SysUser!=null){
            return SysUser.getRealName();
        }
        return null;
    }
}
