package com.sutix.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sutix.common.result.Result;
import com.sutix.common.utils.JwtUtil;
import com.sutix.system.dto.TermAddDTO;
import com.sutix.system.dto.TermAuditDTO;
import com.sutix.system.dto.TermUpdateDTO;
import com.sutix.system.entity.SysTerm;
import com.sutix.system.entity.User;
import com.sutix.system.service.SysTermService;
import com.sutix.system.service.UserService;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.sutix.common.utils.AiUtil;
import com.sutix.system.dto.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.sutix.system.entity.LanguageConstant;
import static com.sutix.system.entity.LanguageConstant.RUSSIAN;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "术语模块", description = "术语的新增、查询、审核、AI生成释义等接口")
@RestController
@RequestMapping("/api/term")
public class TermController {

    @Resource
    private SysTermService termService;
    @Resource
    private UserService userService;
    @Resource
    private JwtUtil jwtUtil;

    /**
     * AI智能生成术语释义（核心接口）
     * 接口：GET /api/term/ai/explain
     * 要求：登录后调用，传入术语名称
     */
    @GetMapping("/ai/explain")
    @Operation(
            summary = "AI智能生成术语释义",
            description = "登录后调用，传入术语名称，AI自动生成俄文释义并返回",
            tags = {"AI术语处理"}
    )
    @Parameters({
            @Parameter(
                    name = "termName",
                    in = ParameterIn.QUERY,
                    description = "需要生成释义的术语名称",
                    required = true,
                    example = "人工智能"
            ),
            @Parameter(
                    name = "token",
                    in = ParameterIn.HEADER,
                    description = "用户登录令牌",
                    required = true,
                    example = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.xxx"
            )
    })
    public Result<?> generateAiExplain(
            @RequestParam @Valid String termName, // 术语名称，非空校验
            @RequestHeader("token") String token
    ) throws AiUtil.DeepLXException {
        // 1. 验证登录（解析token，无需权限）
        jwtUtil.getUserIdFromToken(token);
        // 2. 调用AI工具类生成释义
        String explain = AiUtil.translate(termName,RUSSIAN);
        // 3. 封装结果返回
        Map<String, Object> data = new HashMap<>();
        data.put("termName", termName);
        data.put("aiExplain", explain);
        return Result.success(data, "AI释义生成成功");
    }

    /**
     * AI生成释义后直接提交术语（联动接口，提升体验）
     * 接口：POST /api/term/ai/add
     * 要求：登录后调用，传入术语名称，AI生成释义后自动提交为待审核术语
     */
    @PostMapping("/ai/add")
    @Operation(
            summary = "AI生成释义并提交术语",
            description = "登录后调用，传入术语名称，AI生成俄文释义后自动提交为待审核术语",
            tags = {"AI术语处理"}
    )
    @Parameters({
            @Parameter(
                    name = "termName",
                    in = ParameterIn.QUERY,
                    description = "需要生成释义并提交的术语名称",
                    required = true,
                    example = "大数据"
            ),
            @Parameter(
                    name = "token",
                    in = ParameterIn.HEADER,
                    description = "用户登录令牌",
                    required = true,
                    example = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.xxx"
            )
    })
    public Result<?> addTermByAi(
            @RequestParam @Valid String termName, // 术语名称，非空校验
            @RequestHeader("token") String token
    ) throws AiUtil.DeepLXException {
            // 1. 验证登录并获取用户ID
            Long userId = jwtUtil.getUserIdFromToken(token);
            // 2. AI生成释义
            String aiExplain = AiUtil.translate(termName,RUSSIAN);
            // 3. 封装为TermAddDTO，调用原有新增方法提交术语
            TermAddDTO termAddDTO = new TermAddDTO();
            termAddDTO.setTermName(termName);
            termAddDTO.setTermExplain(aiExplain);
            termAddDTO.setCategoryId(0L);
            termService.addTerm(termAddDTO, userId);
            // 4. 封装结果返回
            Map<String, Object> data = new HashMap<>();
            data.put("termName", termName);
            data.put("aiExplain", aiExplain);
            data.put("status", "待审核");
            return Result.success(data, "AI生成释义并提交术语成功，等待审核");
    }

    // 1. 新增术语（普通用户可提交）
    @PostMapping("/add")
    @Operation(
            summary = "新增术语",
            description = "普通用户登录后提交术语，提交后状态为待审核",
            tags = {"术语基础操作"}
    )
    @Parameters({
            @Parameter(
                    name = "termAddDTO",
                    in = ParameterIn.DEFAULT,
                    description = "新增术语的请求参数",
                    required = true,
                    example = "{\"termName\":\"区块链\",\"termExplain\":\"一种分布式账本技术\",\"categoryId\":1}"
            ),
            @Parameter(
                    name = "token",
                    in = ParameterIn.HEADER,
                    description = "用户登录令牌",
                    required = true,
                    example = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.xxx"
            )
    })
    public Result<?> addTerm(
            @Valid @RequestBody TermAddDTO termAddDTO,
            @RequestHeader("token") String token
    ) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        termService.addTerm(termAddDTO, userId);
        return Result.success("术语提交成功，等待审核");
    }

    @GetMapping("/list")
    @Operation(
            summary = "分页查询术语列表",
            description = "登录后查询术语列表，支持关键词模糊搜索和分类筛选",
            tags = {"术语基础操作"}
    )
    @Parameters({
            @Parameter(
                    name = "pageNum",
                    in = ParameterIn.QUERY,
                    description = "页码，默认值1",
                    required = false,
                    example = "1"
            ),
            @Parameter(
                    name = "pageSize",
                    in = ParameterIn.QUERY,
                    description = "每页条数，默认值10",
                    required = false,
                    example = "10"
            ),
            @Parameter(
                    name = "keyword",
                    in = ParameterIn.QUERY,
                    description = "搜索关键词（匹配术语名称/释义）",
                    required = false,
                    example = "智能"
            ),
            @Parameter(
                    name = "categoryId",
                    in = ParameterIn.QUERY,
                    description = "术语分类ID",
                    required = false,
                    example = "1"
            ),
            @Parameter(
                    name = "token",
                    in = ParameterIn.HEADER,
                    description = "用户登录令牌",
                    required = true,
                    example = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.xxx"
            )
    })
    public Result<?> listTerms(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId, // 新增分类参数
            @RequestHeader("token") String token
    ) {
        jwtUtil.getUserIdFromToken(token);
        Page<SysTerm> page = new Page<>(pageNum, pageSize);
        IPage<SysTerm> termPage = termService.listTerms(page, keyword, categoryId); // 传categoryId
        return Result.success(termPage);
    }

    // 3. 管理员审核术语
    @PostMapping("/admin/audit")
    @Operation(
            summary = "管理员审核术语",
            description = "仅管理员可操作，审核术语通过/驳回",
            tags = {"术语管理操作"}
    )
    @Parameters({
            @Parameter(
                    name = "termAuditDTO",
                    in = ParameterIn.DEFAULT,
                    description = "审核术语的请求参数",
                    required = true,
                    example = "{\"termId\":1,\"auditStatus\":1,\"auditRemark\":\"审核通过\"}"
            ),
            @Parameter(
                    name = "token",
                    in = ParameterIn.HEADER,
                    description = "管理员登录令牌",
                    required = true,
                    example = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.xxx"
            )
    })
    public Result<?> auditTerm(
            @Valid @RequestBody TermAuditDTO termAuditDTO,
            @RequestHeader("token") String token
    ) {
        // 校验管理员权限
        Long userId = jwtUtil.getUserIdFromToken(token);
        if (!userService.isAdmin(userId)) {
            return Result.error(403, "仅管理员可审核术语");
        }
        termService.auditTerm(termAuditDTO);
        return Result.success("术语审核完成");
    }

    // 4. 修改术语（仅创建人+未审核可改）
    @PutMapping("/update")
    @Operation(
            summary = "修改术语",
            description = "仅术语创建人且术语未审核时可修改",
            tags = {"术语基础操作"}
    )
    @Parameters({
            @Parameter(
                    name = "termUpdateDTO",
                    in = ParameterIn.DEFAULT,
                    description = "修改术语的请求参数",
                    required = true,
                    example = "{\"termId\":1,\"termName\":\"人工智能\",\"termExplain\":\"模拟人类智能的技术\",\"categoryId\":1}"
            ),
            @Parameter(
                    name = "token",
                    in = ParameterIn.HEADER,
                    description = "用户登录令牌",
                    required = true,
                    example = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.xxx"
            )
    })
    public Result<?> updateTerm(
            @Valid @RequestBody TermUpdateDTO termUpdateDTO,
            @RequestHeader("token") String token
    ) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        termService.updateTerm(termUpdateDTO, userId);
        return Result.success("术语修改成功");
    }

    // 5. 删除术语（仅创建人/管理员可删）
    @DeleteMapping("/delete/{termId}")
    @Operation(
            summary = "删除术语",
            description = "术语创建人或管理员可删除术语",
            tags = {"术语基础操作", "术语管理操作"}
    )
    @Parameters({
            @Parameter(
                    name = "termId",
                    in = ParameterIn.PATH,
                    description = "需要删除的术语ID",
                    required = true,
                    example = "1"
            ),
            @Parameter(
                    name = "token",
                    in = ParameterIn.HEADER,
                    description = "用户登录令牌",
                    required = true,
                    example = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.xxx"
            )
    })
    public Result<?> deleteTerm(
            @PathVariable Long termId,
            @RequestHeader("token") String token
    ) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        boolean isAdmin = userService.isAdmin(userId);
        termService.deleteTerm(termId, userId, isAdmin);
        return Result.success("术语删除成功");
    }

    @Operation(summary = "导出术语到Excel", description = "所有登录用户可导出，支持关键词/分类筛选")
    @GetMapping("/export")
    public void exportTerms(
            @Parameter(description = "关键词（术语名称模糊匹配）") @RequestParam(required = false) String keyword,
            @Parameter(description = "分类ID（筛选指定分类的术语）") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "登录token", required = true) @RequestHeader("token") String token,
            HttpServletResponse response
    ) throws IOException {
        // 验证登录
        jwtUtil.getUserIdFromToken(token);
        // 导出Excel
        termService.exportTerms(response, keyword, categoryId);
    }

    @Operation(summary = "从Excel导入术语", description = "仅管理员可操作，批量导入术语（状态为待审核）")
    @PostMapping("/import")
    public Result<?> importTerms(
            @Parameter(description = "Excel文件", required = true) @RequestParam("file") MultipartFile file,
            @Parameter(description = "登录token", required = true) @RequestHeader("token") String token
    ) throws IOException {
        // 验证管理员权限
        Long userId = jwtUtil.getUserIdFromToken(token);
        if (!userService.isAdmin(userId)) {
            return Result.error(403, "仅管理员可批量导入术语");
        }
        // 校验文件非空
        if (file.isEmpty()) {
            return Result.error(400, "导入失败：文件不能为空");
        }
        // 导入Excel
        termService.importTerms(file.getInputStream(), userId);
        return Result.success("术语批量导入成功");
    }
}