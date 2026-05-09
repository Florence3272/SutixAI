package com.sutix.system.controller;

import com.sutix.common.result.Result;
import com.sutix.common.utils.JwtUtil;
import com.sutix.system.dto.CategoryDTO;
import com.sutix.system.entity.SysTermCategory;
import com.sutix.system.service.SysTermCategoryService;
import com.sutix.system.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "分类管理接口", description = "分类的新增、修改、删除、查询等操作")
@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Resource
    private SysTermCategoryService categoryService;
    @Resource
    private UserService userService;
    @Resource
    private JwtUtil jwtUtil;

    // 1. 新增/修改分类（仅管理员）
    @PostMapping("/saveOrUpdate")
    @Operation(
            summary = "新增/修改分类",
            description = "管理员专属接口，用于新增分类或修改已有分类信息",
            // 请求头参数注解
            parameters = {
                    @Parameter(
                            name = "token",
                            description = "用户登录令牌",
                            required = true,
                            in = ParameterIn.HEADER,
                            example = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsImV4cCI6MTcxNzIwMDAwMH0.xxxx"
                    )
            }
    )
    public Result<?> saveOrUpdateCategory(
            @Valid @RequestBody CategoryDTO categoryDTO,
            @RequestHeader("token") String token
    ) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        if (!userService.isAdmin(userId)) {
            return Result.error(403, "仅管理员可管理分类");
        }
        categoryService.saveOrUpdateCategory(categoryDTO, userId);
        return Result.success(categoryDTO.getId() == null ? "分类新增成功" : "分类修改成功");
    }

    // 2. 删除分类（仅管理员）
    @DeleteMapping("/delete/{categoryId}")
    @Operation(
            summary = "删除分类",
            description = "管理员专属接口，根据分类ID删除指定分类",
            parameters = {
                    @Parameter(
                            name = "token",
                            description = "用户登录令牌",
                            required = true,
                            in = ParameterIn.HEADER,
                            example = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsImV4cCI6MTcxNzIwMDAwMH0.xxxx"
                    ),
                    @Parameter(
                            name = "categoryId",
                            description = "分类ID",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "1001",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(type = "integer")
                    )
            }
    )
    public Result<?> deleteCategory(
            @PathVariable Long categoryId,
            @RequestHeader("token") String token
    ) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        if (!userService.isAdmin(userId)) {
            return Result.error(403, "仅管理员可删除分类");
        }
        categoryService.deleteCategory(categoryId);
        return Result.success("分类删除成功");
    }

    // 3. 查询所有分类（所有登录用户）
    @GetMapping("/list")
    @Operation(
            summary = "查询所有分类",
            description = "所有登录用户均可访问，查询系统中所有的分类列表",
            parameters = {
                    @Parameter(
                            name = "token",
                            description = "用户登录令牌",
                            required = true,
                            in = ParameterIn.HEADER,
                            example = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsImV4cCI6MTcxNzIwMDAwMH0.xxxx"
                    )
            }
    )
    public Result<?> listAllCategories(@RequestHeader("token") String token) {
        // 仅验证登录，不校验权限
        jwtUtil.getUserIdFromToken(token);
        List<SysTermCategory> categoryList = categoryService.listAllCategories();
        return Result.success(categoryList);
    }
}