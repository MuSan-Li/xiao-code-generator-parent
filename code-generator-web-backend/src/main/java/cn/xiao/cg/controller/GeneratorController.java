package cn.xiao.cg.controller;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.json.JSONUtil;
import cn.xiao.cg.annotation.AuthCheck;
import cn.xiao.cg.common.BaseResponse;
import cn.xiao.cg.common.DeleteRequest;
import cn.xiao.cg.common.ErrorCode;
import cn.xiao.cg.common.ResultUtils;
import cn.xiao.cg.constant.UserConstant;
import cn.xiao.cg.exception.BusinessException;
import cn.xiao.cg.exception.ThrowUtils;
import cn.xiao.cg.manager.CacheManager;
import cn.xiao.cg.manager.CosManager;
import cn.xiao.cg.model.dto.generator.GeneratorAddRequest;
import cn.xiao.cg.model.dto.generator.GeneratorCacheRequest;
import cn.xiao.cg.model.dto.generator.GeneratorEditRequest;
import cn.xiao.cg.model.dto.generator.GeneratorMakeRequest;
import cn.xiao.cg.model.dto.generator.GeneratorQueryRequest;
import cn.xiao.cg.model.dto.generator.GeneratorUpdateRequest;
import cn.xiao.cg.model.dto.generator.GeneratorUseRequest;
import cn.xiao.cg.model.entity.Generator;
import cn.xiao.cg.model.entity.User;
import cn.xiao.cg.model.vo.GeneratorVO;
import cn.xiao.cg.service.GeneratorService;
import cn.xiao.cg.service.UserService;
import cn.xiao.maker.generator.main.GenerateTemplate;
import cn.xiao.maker.generator.main.ZipGenerator;
import cn.xiao.maker.meta.Meta;
import cn.xiao.maker.meta.MetaValidator;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.utils.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * 帖子接口
 *
 * @author xiao
 */
@RestController
@RequestMapping("/generator")
@Slf4j
public class GeneratorController {

    @Resource
    private GeneratorService generatorService;
    @Resource
    private UserService userService;

    @Resource
    private CosManager cosManager;

    @Resource
    private CacheManager cacheManager;

    // region 增删改查

    /**
     * 创建
     *
     * @param addRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addGenerator(@RequestBody GeneratorAddRequest addRequest, HttpServletRequest request) {
        if (Objects.isNull(addRequest)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Generator generator = new Generator();
        BeanUtils.copyProperties(addRequest, generator);
        List<String> tags = addRequest.getTags();
        if (ObjectUtil.isNotEmpty(tags)) {
            generator.setTags(JSONUtil.toJsonStr(tags));
        }
        generator.setTags(JSONUtil.toJsonStr(tags));
        Meta.FileConfigDTO fileConfig = addRequest.getFileConfig();
        generator.setFileConfig(JSONUtil.toJsonStr(fileConfig));
        Meta.ModelConfigDTO modelConfig = addRequest.getModelConfig();
        generator.setModelConfig(JSONUtil.toJsonStr(modelConfig));

        // 参数校验
        generatorService.validGenerator(generator, true);
        User loginUser = userService.getLoginUser(request);
        generator.setUserId(loginUser.getId());
        boolean result = generatorService.save(generator);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newGeneratorId = generator.getId();
        return ResultUtils.success(newGeneratorId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteGenerator(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (Objects.isNull(deleteRequest) || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Generator oldGenerator = generatorService.getById(id);
        ThrowUtils.throwIf(Objects.isNull(oldGenerator), ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldGenerator.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = generatorService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param updateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateGenerator(@RequestBody GeneratorUpdateRequest updateRequest) {
        if (Objects.isNull(updateRequest) || updateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Generator generator = new Generator();
        BeanUtils.copyProperties(updateRequest, generator);
        List<String> tags = updateRequest.getTags();
        if (ObjectUtil.isNotEmpty(tags)) {
            generator.setTags(JSONUtil.toJsonStr(tags));
        }
        generator.setTags(JSONUtil.toJsonStr(tags));
        Meta.FileConfigDTO fileConfig = updateRequest.getFileConfig();
        generator.setFileConfig(JSONUtil.toJsonStr(fileConfig));
        Meta.ModelConfigDTO modelConfig = updateRequest.getModelConfig();
        generator.setModelConfig(JSONUtil.toJsonStr(modelConfig));

        // 参数校验
        generatorService.validGenerator(generator, false);
        long id = updateRequest.getId();
        // 判断是否存在
        Generator oldGenerator = generatorService.getById(id);
        ThrowUtils.throwIf(Objects.isNull(oldGenerator), ErrorCode.NOT_FOUND_ERROR);
        boolean result = generatorService.updateById(generator);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<GeneratorVO> getGeneratorVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Generator generator = generatorService.getById(id);
        if (Objects.isNull(generator)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(generatorService.getGeneratorVO(generator, request));
    }

    /**
     * 分页获取列表（仅管理员）
     *
     * @param queryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Generator>> listGeneratorByPage(@RequestBody GeneratorQueryRequest queryRequest) {
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        Page<Generator> generatorPage = generatorService.page(new Page<>(current, size), generatorService.getQueryWrapper(queryRequest));
        return ResultUtils.success(generatorPage);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param queryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<GeneratorVO>> listGeneratorVOByPage(@RequestBody GeneratorQueryRequest queryRequest,
                                                                 HttpServletRequest request) {
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Generator> generatorPage = generatorService.page(new Page<>(current, size),
                generatorService.getQueryWrapper(queryRequest));
        return ResultUtils.success(generatorService.getGeneratorVOPage(generatorPage, request));
    }

    /**
     * 快速分页获取列表（封装类）
     *
     * @param generatorQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo/fast")
    public BaseResponse<Page<GeneratorVO>> listGeneratorVOByPageFast(@RequestBody GeneratorQueryRequest generatorQueryRequest,
                                                                     HttpServletRequest request) {
        long current = generatorQueryRequest.getCurrent();
        long size = generatorQueryRequest.getPageSize();
        // 优先从缓存读取
        String cacheKey = getPageCacheKey(generatorQueryRequest);
        Object cacheValue = cacheManager.get(cacheKey);
        if (cacheValue != null) {
            return ResultUtils.success((Page<GeneratorVO>) cacheValue);
        }

        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        LambdaQueryWrapper<Generator> queryWrapper = generatorService.getQueryWrapper(generatorQueryRequest);
        queryWrapper.select(Generator::getId, Generator::getName, Generator::getDescription,
                Generator::getTags, Generator::getPicture, Generator::getStatus,
                Generator::getUserId, Generator::getCreateTime, Generator::getUpdateTime);
        Page<Generator> generatorPage = generatorService.page(new Page<>(current, size), queryWrapper);
        Page<GeneratorVO> generatorVOPage = generatorService.getGeneratorVOPage(generatorPage, request);
        // 写入缓存
        cacheManager.put(cacheKey, generatorVOPage);
        return ResultUtils.success(generatorVOPage);
    }


    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param queryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<GeneratorVO>> listMyGeneratorVOByPage(@RequestBody GeneratorQueryRequest queryRequest, HttpServletRequest request) {
        if (Objects.isNull(queryRequest)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        queryRequest.setUserId(loginUser.getId());
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Generator> generatorPage = generatorService.page(new Page<>(current, size), generatorService.getQueryWrapper(queryRequest));
        return ResultUtils.success(generatorService.getGeneratorVOPage(generatorPage, request));
    }

    // endregion

    /**
     * 编辑（用户）
     *
     * @param editRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editGenerator(@RequestBody GeneratorEditRequest editRequest, HttpServletRequest request) {
        if (Objects.isNull(editRequest) || editRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Generator generator = new Generator();
        BeanUtils.copyProperties(editRequest, generator);
        List<String> tags = editRequest.getTags();
        if (ObjectUtil.isNotEmpty(tags)) {
            generator.setTags(JSONUtil.toJsonStr(tags));
        }

        generator.setTags(JSONUtil.toJsonStr(tags));
        Meta.FileConfigDTO fileConfig = editRequest.getFileConfig();
        generator.setFileConfig(JSONUtil.toJsonStr(fileConfig));
        Meta.ModelConfigDTO modelConfig = editRequest.getModelConfig();
        generator.setModelConfig(JSONUtil.toJsonStr(modelConfig));

        // 参数校验
        generatorService.validGenerator(generator, false);
        User loginUser = userService.getLoginUser(request);
        long id = editRequest.getId();
        // 判断是否存在
        Generator oldGenerator = generatorService.getById(id);
        ThrowUtils.throwIf(Objects.isNull(oldGenerator), ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldGenerator.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = generatorService.updateById(generator);
        // 清理缓存
        if (result) {
            String cacheFileDir = getCacheFileDir(id);
            FileUtil.del(cacheFileDir);
        }
        return ResultUtils.success(result);
    }


    /**
     * 根据 id 下载
     *
     * @param id
     * @return
     */
    @GetMapping("/download")
    public void downloadGeneratorById(long id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Generator generator = generatorService.getById(id);
        if (generator == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        String filepath = generator.getDistPath();
        if (StrUtil.isBlank(filepath)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "产物包不存在");
        }
        // 追踪事件
        log.info("用户 {} 下载了 {}", loginUser, filepath);

        // 设置响应头
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + filepath);

        String zipFilePath = getCacheFilePath(id, filepath);
        if (FileUtil.exist(zipFilePath)) {
            // 写入响应
            Files.copy(Paths.get(zipFilePath), response.getOutputStream());
            return;
        }

        COSObjectInputStream cosObjectInput = null;
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            COSObject cosObject = cosManager.getObject(filepath);
            cosObjectInput = cosObject.getObjectContent();

            // 处理下载到的流
            byte[] bytes = IOUtils.toByteArray(cosObjectInput);

            stopWatch.stop();
            System.out.println("下载耗时：" + stopWatch.getTotalTimeMillis());

            // 写入响应
            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("file download error, filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载失败");
        } finally {
            if (cosObjectInput != null) {
                cosObjectInput.close();
            }
        }
    }


    /**
     * 使用代码生成器
     *
     * @param generatorUseRequest
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/use")
    public void useGenerator(@RequestBody GeneratorUseRequest generatorUseRequest, HttpServletRequest request,
                             HttpServletResponse response) throws IOException {
        // 获取用户输入的请求参数
        Long id = generatorUseRequest.getId();
        Map<String, Object> dataModel = generatorUseRequest.getDataModel();
        // 需要用户登录
        User loginUser = userService.getLoginUser(request);
        log.info("userId = {} 使用了生成器 id = {}", loginUser.getId(), id);
        Generator generator = generatorService.getById(id);
        if (generator == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 生成器的存储路径
        String distPath = generator.getDistPath();
        if (CharSequenceUtil.isBlank(distPath)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "产物包不存在");
        }
        // 从对象存储下载生成器的压缩包
        // 定义独立的工作空间
        String projectPath = System.getProperty("user.dir");
        // 创建临时目录
        // 必须要用 userId 区分，否则可能会导致输入参数文件冲突
        String tempDirPath = String.format("%s/.temp/use/%s/%s", projectPath, id, loginUser.getId());
        String zipFilePath = tempDirPath + "/dist.zip";
        // 目录不存在则创建
        if (!FileUtil.exist(tempDirPath)) {
            FileUtil.mkdir(tempDirPath);
        }

        // 使用文件缓存
        String cacheFilePath = getCacheFilePath(id, distPath);
        Path cacheFilePathObj = Paths.get(cacheFilePath);
        Path zipFilePathObj = Paths.get(zipFilePath);

        if (!FileUtil.exist(zipFilePath)) {
            // 有缓存，复制文件
            if (FileUtil.exist(cacheFilePath)) {
                Files.copy(cacheFilePathObj, zipFilePathObj);
            } else {
                // 没有缓存，从对象存储下载文件
                FileUtil.touch(zipFilePath);
                try {
                    cosManager.download(distPath, zipFilePath);
                } catch (Exception e) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成器下载失败");
                }
                // 写文件缓存
                File parentFile = cacheFilePathObj.toFile().getParentFile();
                if (!FileUtil.exist(parentFile)) {
                    FileUtil.mkdir(parentFile);
                }
                Files.copy(zipFilePathObj, cacheFilePathObj);
            }
        }

        // 解压压缩包，得到脚本文件
        File unzipDistDir = ZipUtil.unzip(zipFilePath);
        // 将用户输入的参数写到 json 文件中
        String dataModelFilePath = tempDirPath + "/dataModel.json";
        String jsonStr = JSONUtil.toJsonStr(dataModel);
        FileUtil.writeUtf8String(jsonStr, dataModelFilePath);
        // 执行脚本
        // 找到脚本文件所在路径
        // 要注意，如果不是 windows 系统，找 generator 文件而不是 bat
        String generatorScriptFileName = getGeneratorScriptCommandByOS();
        File scriptFile = FileUtil.loopFiles(unzipDistDir, 2, null).stream()
                .filter(file -> file.isFile() && generatorScriptFileName.equals(file.getName()))
                .findFirst()
                .orElseThrow(RuntimeException::new);
        // 添加可执行权限
        try {
            Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxrwxrwx");
            Files.setPosixFilePermissions(scriptFile.toPath(), permissions);
        } catch (Exception e) {
            log.error("设置脚本文件权限失败");
        }
        // 构造命令
        File scriptDir = scriptFile.getParentFile();
        // 注意，如果是 mac / linux 系统，要用 "./generator"
        String scriptAbsolutePath = scriptFile.getAbsolutePath().replace("\\", "/");
        String[] commands = new String[]{scriptAbsolutePath, "json-generate", "--file=" + dataModelFilePath};
        // 这里一定要拆分！
        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        processBuilder.directory(scriptDir);
        try {
            Process process = processBuilder.start();
            // 读取命令的输出
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            reader.lines().forEach(line -> log.info("line => {}", line));
            // 等待命令执行完成
            int exitCode = process.waitFor();
            log.info("命令执行结束，退出码：{}", exitCode);
        } catch (Exception e) {
            log.error("执行生成器脚本错误,", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "执行生成器脚本错误");
        }
        // 压缩得到的生成结果，返回给前端
        String generatedPath = scriptDir.getAbsolutePath() + "/generated";
        String resultPath = tempDirPath + "/result.zip";
        File resultFile = ZipUtil.zip(generatedPath, resultPath);
        // 设置响应头
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + resultFile.getName());
        Files.copy(resultFile.toPath(), response.getOutputStream());
        // 清理文件
        CompletableFuture.runAsync(() -> FileUtil.del(tempDirPath));
    }

    /**
     * 制作代码生成器 * *
     *
     * @param generatorMakeRequest
     * @param request
     * @param response
     */
    @PostMapping("/make")
    public void makeGenerator(@RequestBody GeneratorMakeRequest generatorMakeRequest,
                              HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 1）输入参数
        String zipFilePath = generatorMakeRequest.getZipFilePath();
        Meta meta = generatorMakeRequest.getMeta();
        // 需要登录
        userService.getLoginUser(request);
        // 2）创建独立工作空间，下载压缩包到本地
        if (CharSequenceUtil.isBlank(zipFilePath)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "压缩包不存在");
        }
        // 工作空间
        String projectPath = System.getProperty("user.dir");
        // 随机 id
        String id = IdUtil.getSnowflakeNextId() + RandomUtil.randomString(6);
        String tempDirPath = String.format("%s/.temp/make/%s", projectPath, id);
        String localZipFilePath = tempDirPath + "/project.zip";
        // 新建文件
        if (!FileUtil.exist(localZipFilePath)) {
            FileUtil.touch(localZipFilePath);
        }
        try {
            cosManager.download(zipFilePath, localZipFilePath);
        } catch (InterruptedException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "压缩包下载失败");
        }
        // 3）解压，得到项目模板文件
        File unzipDistDir = ZipUtil.unzip(localZipFilePath);
        // 4）构造 meta 对象和输出路径
        String sourceRootPath = unzipDistDir.getAbsolutePath();
        meta.getFileConfig().setSourceRootPath(sourceRootPath);
        MetaValidator.doValidaAndFill(meta);
        String outputPath = String.format("%s/generated/%s", tempDirPath, meta.getName());
        // 5）调用 maker 方法制作生成器
        GenerateTemplate generateTemplate = new ZipGenerator();
        try {
            generateTemplate.doGenerate(meta, outputPath);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "制作失败");
        }
        // 6）下载压缩的产物包文件
        String suffix = "-dist.zip";
        String zipFileName = meta.getName() + suffix;
        String distZipFilePath = outputPath + suffix;
        // 下载文件 设置响应头
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + zipFileName);
        // 写入响应
        Files.copy(Paths.get(distZipFilePath), response.getOutputStream());
        // 7）清理文件
        CompletableFuture.runAsync(() -> FileUtil.del(tempDirPath));
    }

    /**
     * 缓存代码生成器
     *
     * @param generatorCacheRequest
     * @return
     */
    @PostMapping("/cache")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public void cacheGenerator(@RequestBody GeneratorCacheRequest generatorCacheRequest,
                               HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (generatorCacheRequest == null || generatorCacheRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        long id = generatorCacheRequest.getId();
        Generator generator = generatorService.getById(id);
        if (generator == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        String distPath = generator.getDistPath();
        if (CharSequenceUtil.isBlank(distPath)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "产物包不存在");
        }

        String zipFilePath = getCacheFilePath(id, distPath);

        try {
            cosManager.download(distPath, zipFilePath);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成器下载失败");
        }
    }

    /**
     * 获取缓存文件路径
     *
     * @param id
     * @param distPath
     * @return
     */
    public String getCacheFilePath(long id, String distPath) {
        String projectPath = System.getProperty("user.dir");
        String tempDirPath = String.format("%s/.temp/cache/%s", projectPath, id);
        return tempDirPath + "/" + distPath;
    }

    /**
     * 获取分页缓存 keu
     *
     * @param generatorQueryRequest
     * @return
     */
    public static String getPageCacheKey(GeneratorQueryRequest generatorQueryRequest) {
        String jsonStr = JSONUtil.toJsonStr(generatorQueryRequest);
        // 请求参数编码
        String base64 = Base64Encoder.encode(jsonStr);
        return "generator:page:" + base64;
    }

    public static String getGeneratorScriptCommandByOS() {
        // win
        String winMvnCommand = "generator.bat";
        // other
        String otherMvnCommand = "generator.sh";
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return winMvnCommand;
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            return otherMvnCommand;
        } else {
            throw new RuntimeException("Unsupported operating system: " + os);
        }
    }

    /**
     * 获取缓存文件所在的目录
     *
     * @param id 生成器 id
     * @return
     */
    public String getCacheFileDir(long id) {
        String projectPath = System.getProperty("user.dir");
        return String.format("%s/.temp/cache/%s", projectPath, id);
    }
}
