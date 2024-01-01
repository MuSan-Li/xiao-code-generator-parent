package cn.xiao.maker.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.xiao.maker.meta.Meta;
import cn.xiao.maker.meta.enums.FileGenerateTypeEnum;
import cn.xiao.maker.meta.enums.FileTypeEnum;
import cn.xiao.maker.template.enums.FileFilterRangeEnum;
import cn.xiao.maker.template.enums.FileFilterRuleEnum;
import cn.xiao.maker.template.model.FileFilterConfig;
import cn.xiao.maker.template.model.TemplateMakerFileConfig;
import cn.xiao.maker.template.model.TemplateMakerModelConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 模板制作工具
 *
 * @author xiao
 */
public class TemplateMaker {

    private TemplateMaker() {
    }

    public static void main(String[] args) {

        Meta meta = new Meta();
        // meta.setName("acm-template-pro-generator");
        // meta.setDescription("acm-示例代码生成器");

        meta.setName("springboot-init-generator");
        meta.setDescription("SpringBoot-初始化项目代码生成器");

        String projectPath = System.getProperty("user.dir");
        String originRootPath = new File(projectPath).getParent() + File.separator + "code-generator-template/springboot-init-template";
        String fileInputPath1 = "src/main/java/cn/xiao/springbootinit/common";
        String fileInputPath2 = "src/main/java/cn/xiao/springbootinit/controller";
        String fileInputPath3 = "src/main/resources";

        TemplateMakerFileConfig templateMakerFileConfig = new TemplateMakerFileConfig();
        List<TemplateMakerFileConfig.FileInfoConfig> fileInfoConfigList = new ArrayList<>();
        TemplateMakerFileConfig.FileInfoConfig config = new TemplateMakerFileConfig.FileInfoConfig();
        List<FileFilterConfig> filterConfigList = new ArrayList<>();
        filterConfigList.add(FileFilterConfig.builder()
                .range(FileFilterRangeEnum.FILE_CONTENT.getValue())
                .value("url")
                .rule(FileFilterRuleEnum.CONTAINS.getValue())
                .build());
        config.setFilterConfigList(filterConfigList);
        config.setPath(fileInputPath3);
        fileInfoConfigList.add(config);

        TemplateMakerFileConfig.FileInfoConfig config2 = new TemplateMakerFileConfig.FileInfoConfig();
        List<FileFilterConfig> filterConfigList2 = new ArrayList<>();
        filterConfigList2.add(FileFilterConfig.builder()
                .range(FileFilterRangeEnum.FILE_CONTENT.getValue())
                .value("BaseResponse")
                .rule(FileFilterRuleEnum.CONTAINS.getValue())
                .build());
        config2.setFilterConfigList(filterConfigList2);
        config2.setPath(fileInputPath1);
        fileInfoConfigList.add(config2);
        templateMakerFileConfig.setFileInfoConfigList(fileInfoConfigList);

        // test 第一次
        // Meta.ModelConfigDTO.ModelsDTO modelsDTO = new Meta.ModelConfigDTO.ModelsDTO();
        // modelsDTO.setFieldName("outputText");
        // modelsDTO.setType("String");
        // modelsDTO.setDefaultValue("Sum: ");
        // String searchStr = "Sum: ";

        // test 第二次
        Meta.ModelConfigDTO.ModelsDTO modelsDTO = new Meta.ModelConfigDTO.ModelsDTO();
        modelsDTO.setFieldName("className");
        modelsDTO.setType("String");
        modelsDTO.setDefaultValue("Sum: ");

        // 分组配置
        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = new TemplateMakerFileConfig.FileGroupConfig();
        fileGroupConfig.setCondition("outputText");
        fileGroupConfig.setGroupKey("test");
        fileGroupConfig.setGroupName("测试分组");
        templateMakerFileConfig.setFileGroupConfig(fileGroupConfig);

        // 模型参数配置
        TemplateMakerModelConfig templateMakerModelConfig = new TemplateMakerModelConfig();

        // 模型组配置
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = new TemplateMakerModelConfig.ModelGroupConfig();
        modelGroupConfig.setGroupKey("mysql");
        modelGroupConfig.setGroupName("数据库配置");
        templateMakerModelConfig.setModelGroupConfig(modelGroupConfig);

        // 模型配置
        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig1 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig1.setFieldName("url");
        modelInfoConfig1.setType("String");
        modelInfoConfig1.setDefaultValue("jdbc:mysql://localhost:3306/my_db");
        modelInfoConfig1.setReplaceText("jdbc:mysql://localhost:3306/my_db");
        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig2 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig2.setFieldName("username");
        modelInfoConfig2.setType("String");
        modelInfoConfig2.setDefaultValue("root");
        modelInfoConfig2.setReplaceText("root");
        List<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfigList = Arrays.asList(modelInfoConfig1, modelInfoConfig2);
        templateMakerModelConfig.setModels(modelInfoConfigList);

        long id = makeTemplate(meta, originRootPath, templateMakerFileConfig, templateMakerModelConfig, 1L);
        System.out.println(id);
        System.out.println("============= success =============");
    }

    private static long makeTemplate(Meta newMeta, String originRootPath,
                                     TemplateMakerFileConfig templateMakerFileConfig,
                                     TemplateMakerModelConfig templateMakerModelConfig, Long id) {
        // id为空则直生成
        if (Objects.isNull(id)) {
            id = IdUtil.getSnowflakeNextId();
        }
        // 其他业务逻辑
        // 1.输入信息
        // 1.1输入文件信息
        String projectPath = System.getProperty("user.dir");
        String originLastFileName = new File(originRootPath).getName();

        // 1.2复制原始模板到工作空间
        String workSpace = ".temp";
        String tempDirPath = projectPath + File.separator + workSpace;
        // String templateDirPath = tempDirPath + File.separator + originLastFileName + StrPool.DASHED + id;
        String templateDirPath = tempDirPath + File.separator + id;
        if (!FileUtil.exist(templateDirPath)) {
            FileUtil.mkdir(templateDirPath);
            FileUtil.copy(originRootPath, templateDirPath, true);
        }

        // 生成文件模型

        String sourceRootPath = templateDirPath + File.separator + originLastFileName;
        // win 路径转义
        sourceRootPath = sourceRootPath.replaceAll("\\\\", "/");
        List<TemplateMakerFileConfig.FileInfoConfig> fileList = templateMakerFileConfig.getFileInfoConfigList();


        List<Meta.FileConfigDTO.FilesDTO> newFilesDTOList = new ArrayList<>();
        for (TemplateMakerFileConfig.FileInfoConfig fileInfoConfig : fileList) {
            String fileInputPath = fileInfoConfig.getPath();
            // 如果填写的是相对路径要改为绝对路径
            if (!fileInputPath.startsWith(sourceRootPath)) {
                fileInputPath = sourceRootPath + File.separator + fileInputPath;
            }
            List<File> files = TemplateFileFilter.doFilter(fileInputPath, fileInfoConfig.getFilterConfigList());
            if (CollUtil.isEmpty(files)) {
                continue;
            }
            for (File file : files) {
                newFilesDTOList.add(makeFileTemplate(sourceRootPath, file, templateMakerModelConfig));
            }
        }
        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = templateMakerFileConfig.getFileGroupConfig();

        if (Objects.nonNull(fileGroupConfig)) {
            String condition = fileGroupConfig.getCondition();
            String groupKey = fileGroupConfig.getGroupKey();
            String groupName = fileGroupConfig.getGroupName();
            Meta.FileConfigDTO.FilesDTO fileConfigDTO = new Meta.FileConfigDTO.FilesDTO();
            fileConfigDTO.setType(FileTypeEnum.GROUP.getValue());
            fileConfigDTO.setCondition(condition);
            fileConfigDTO.setGroupKey(groupKey);
            fileConfigDTO.setGroupName(groupName);
            fileConfigDTO.setFiles(newFilesDTOList);
            newFilesDTOList = new ArrayList<>();
            newFilesDTOList.add(fileConfigDTO);
        }

        // 处理模型信息
        List<TemplateMakerModelConfig.ModelInfoConfig> models = templateMakerModelConfig.getModels();
        //  对象转换 ModelInfoConfig => ModelsDTO
        List<Meta.ModelConfigDTO.ModelsDTO> inputModelsDTOList = models.stream().map(item -> {
            Meta.ModelConfigDTO.ModelsDTO modelsDTO = new Meta.ModelConfigDTO.ModelsDTO();
            BeanUtil.copyProperties(item, modelsDTO);
            return modelsDTO;
        }).collect(Collectors.toList());

        // 本次新增的模型配置列表
        List<Meta.ModelConfigDTO.ModelsDTO> newModelsDTOList = new ArrayList<>();
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();

        // - 如果是模型组 全放到一个分组内
        if (Objects.nonNull(modelGroupConfig)) {
            String condition = modelGroupConfig.getCondition();
            String groupKey = modelGroupConfig.getGroupKey();
            String groupName = modelGroupConfig.getGroupName();
            Meta.ModelConfigDTO.ModelsDTO groupModelsDTO = new Meta.ModelConfigDTO.ModelsDTO();
            groupModelsDTO.setGroupKey(groupKey);
            groupModelsDTO.setGroupName(groupName);
            groupModelsDTO.setCondition(condition);
            groupModelsDTO.setModels(inputModelsDTOList);
            newModelsDTOList.add(groupModelsDTO);
        } else {
            // 不分组，添加所有的模型信息到列表
            newModelsDTOList.addAll(inputModelsDTOList);
        }

        // 3生成配置文件
        String metaOutPath = sourceRootPath + File.separator + "meta.json";

        if (FileUtil.exist(metaOutPath)) {
            newMeta = JSONUtil.toBean(FileUtil.readUtf8String(metaOutPath), Meta.class);

            List<Meta.FileConfigDTO.FilesDTO> filesDTOList = newMeta.getFileConfig().getFiles();
            filesDTOList.addAll(newFilesDTOList);
            List<Meta.ModelConfigDTO.ModelsDTO> modelsDTOList = newMeta.getModelConfig().getModels();
            modelsDTOList.addAll(newModelsDTOList);

            // 去重配置
            newMeta.getFileConfig().setFiles(distinctFile(filesDTOList));
            newMeta.getModelConfig().setModels(distinctModel(modelsDTOList));

            FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newMeta), metaOutPath);
            return id;
        }

        Meta.FileConfigDTO fileConfigDTO = new Meta.FileConfigDTO();
        fileConfigDTO.setSourceRootPath(sourceRootPath);

        // 3.1构造file配置参数
        fileConfigDTO.setFiles(newFilesDTOList);
        newMeta.setFileConfig(fileConfigDTO);

        // 3.2构造model配置参数
        Meta.ModelConfigDTO modelConfigDTO = new Meta.ModelConfigDTO();
        modelConfigDTO.setModels(newModelsDTOList);
        newMeta.setModelConfig(modelConfigDTO);

        // 3.3创建元信息文件
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newMeta), metaOutPath);

        System.out.println("============= success =============");
        return id;
    }

    /**
     * @param sourceRootPath
     * @param inputFile
     * @param templateMakerModelConfig
     * @return
     */
    private static Meta.FileConfigDTO.FilesDTO makeFileTemplate(String sourceRootPath, File inputFile,
                                                                TemplateMakerModelConfig templateMakerModelConfig) {
        // 举例：D:\Project\xiao-code-generator-parent\code-generator-maker\src\main\java\cn\xiao\xxx
        // win系统对路径转义
        String fileInputAbsolutePath = inputFile.getAbsolutePath().replaceAll("\\\\", "/");
        String fileOutputAbsolutePath = fileInputAbsolutePath + ".ftl";

        // 举例：src/main/java/cn/xiao/xxx
        // 文件输入输出替换为相对路径
        String fileInputPath = fileInputAbsolutePath.replace(sourceRootPath + "/", "");
        String fileOutputPath = fileInputPath + ".ftl";

        boolean existFile = FileUtil.exist(fileOutputAbsolutePath);
        String fileContent = FileUtil.readUtf8String(existFile ? fileOutputAbsolutePath : fileInputAbsolutePath);
        String replacement;
        String newFileContent = fileContent;
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        List<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfigList = templateMakerModelConfig.getModels();

        for (TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig : modelInfoConfigList) {
            // 不是分组
            if (Objects.isNull(modelGroupConfig)) {
                replacement = String.format("${%s}", modelInfoConfig.getFieldName());
            } else {
                // 注意挖坑要多一个层级
                replacement = String.format("${%s.%s}", modelGroupConfig.getGroupKey(), modelInfoConfig.getFieldName());
            }
            // 多次替换
            newFileContent = StrUtil.replace(newFileContent, modelInfoConfig.getReplaceText(), replacement);
        }


        // 文件参数信息
        Meta.FileConfigDTO.FilesDTO filesDTO = new Meta.FileConfigDTO.FilesDTO();
        filesDTO.setInputPath(fileInputPath);
        filesDTO.setOutputPath(fileOutputPath);
        filesDTO.setType(FileTypeEnum.FILE.getValue());
        filesDTO.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());

        // 修改的文件内容和原文件一样 为静态文件否则动态文件
        if (Objects.equals(fileContent, newFileContent)) {
            filesDTO.setOutputPath(fileInputPath);
            filesDTO.setGenerateType(FileGenerateTypeEnum.STATIC.getValue());
        } else {
            FileUtil.writeUtf8String(newFileContent, fileOutputAbsolutePath);
        }
        return filesDTO;

    }

    /**
     * 制作文件模板
     *
     * @param modelsDTO
     * @param sourceRootPath
     * @param searchStr
     * @param inputFile
     * @return
     */
    private static Meta.FileConfigDTO.FilesDTO makeFileTemplate(
            Meta.ModelConfigDTO.ModelsDTO modelsDTO, String sourceRootPath, String searchStr, File inputFile) {
        // 举例：D:\Project\xiao-code-generator-parent\code-generator-maker\src\main\java\cn\xiao\xxx
        // win系统对路径转义
        String fileInputAbsolutePath = inputFile.getAbsolutePath().replaceAll("\\\\", "/");
        String fileOutputAbsolutePath = fileInputAbsolutePath + ".ftl";

        // 举例：src/main/java/cn/xiao/xxx
        // 文件输入输出替换为相对路径
        String fileInputPath = fileInputAbsolutePath.replace(sourceRootPath + "/", "");
        String fileOutputPath = fileInputPath + ".ftl";

        boolean existFile = FileUtil.exist(fileOutputAbsolutePath);
        String fileContent = FileUtil.readUtf8String(existFile ? fileOutputAbsolutePath : fileInputAbsolutePath);
        String replacement = String.format("${%s}", modelsDTO.getFieldName());
        String newFileContent = StrUtil.replace(fileContent, searchStr, replacement);

        // 文件参数信息
        Meta.FileConfigDTO.FilesDTO filesDTO = new Meta.FileConfigDTO.FilesDTO();
        filesDTO.setInputPath(fileInputPath);
        filesDTO.setOutputPath(fileOutputPath);
        filesDTO.setType(FileTypeEnum.FILE.getValue());
        filesDTO.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());

        // 修改的文件内容和原文件一样 为静态文件否则动态文件
        if (Objects.equals(fileContent, newFileContent)) {
            filesDTO.setOutputPath(fileInputPath);
            filesDTO.setGenerateType(FileGenerateTypeEnum.STATIC.getValue());
        } else {
            FileUtil.writeUtf8String(newFileContent, fileOutputAbsolutePath);
        }
        return filesDTO;
    }

    /**
     * 去重文件
     *
     * @return
     */
    private static List<Meta.FileConfigDTO.FilesDTO> distinctFile
    (List<Meta.FileConfigDTO.FilesDTO> filesDTOList) {
        // 策略：同分组内文件 merge，不同分组保留
        Map<String, List<Meta.FileConfigDTO.FilesDTO>> groupKeyFileInfoListMap = filesDTOList.stream()
                .filter(item -> StrUtil.isNotBlank(item.getGroupKey()))
                .collect(Collectors.groupingBy(Meta.FileConfigDTO.FilesDTO::getGroupKey));

        // 2. 同组内的文件配置合并
        // 保存每个组对应的合并后的对象 map
        Map<String, Meta.FileConfigDTO.FilesDTO> groupKeyMergedFileInfoMap = new HashMap<>();
        for (Map.Entry<String, List<Meta.FileConfigDTO.FilesDTO>> entry : groupKeyFileInfoListMap.entrySet()) {

            List<Meta.FileConfigDTO.FilesDTO> tempFileConfigDtoList = entry.getValue();
            List<Meta.FileConfigDTO.FilesDTO> newFileList = new ArrayList<>(tempFileConfigDtoList.stream()
                    .flatMap(item -> item.getFiles().stream())
                    .collect(Collectors.toMap(Meta.FileConfigDTO.FilesDTO::getInputPath, m -> m, (o, n) -> n)).values());
            Meta.FileConfigDTO.FilesDTO filesDTO = CollUtil.getLast(tempFileConfigDtoList);
            filesDTO.setFiles(newFileList);
            String groupKey = entry.getKey();
            groupKeyMergedFileInfoMap.put(groupKey, filesDTO);
        }

        List<Meta.FileConfigDTO.FilesDTO> resultList = new ArrayList<>(groupKeyMergedFileInfoMap.values());

        List<Meta.FileConfigDTO.FilesDTO> noGroupFileInfoList = filesDTOList.stream()
                .filter(item -> StrUtil.isBlank(item.getGroupKey())).collect(Collectors.toList());

        resultList.addAll(noGroupFileInfoList.stream()
                .collect(Collectors.toMap(
                        Meta.FileConfigDTO.FilesDTO::getInputPath, Function.identity(), (o, n) -> n)
                ).values());
        return resultList;
    }

    /**
     * 去重模型
     *
     * @return
     */
    private static List<Meta.ModelConfigDTO.ModelsDTO> distinctModel
    (List<Meta.ModelConfigDTO.ModelsDTO> modelsDTOList) {
        // 策略：同分组内文件 merge，不同分组保留
        Map<String, List<Meta.ModelConfigDTO.ModelsDTO>> groupKeyFileInfoListMap = modelsDTOList.stream()
                .filter(item -> StrUtil.isNotBlank(item.getGroupKey()))
                .collect(Collectors.groupingBy(Meta.ModelConfigDTO.ModelsDTO::getGroupKey));

        // 2. 同组内的模型配置合并
        // 保存每个组对应的合并后的对象 map
        Map<String, Meta.ModelConfigDTO.ModelsDTO> groupKeyMergedFileInfoMap = new HashMap<>();
        for (Map.Entry<String, List<Meta.ModelConfigDTO.ModelsDTO>> entry : groupKeyFileInfoListMap.entrySet()) {

            List<Meta.ModelConfigDTO.ModelsDTO> tempModelConfigDtoList = entry.getValue();
            List<Meta.ModelConfigDTO.ModelsDTO> newFileList = new ArrayList<>(tempModelConfigDtoList.stream()
                    .flatMap(item -> item.getModels().stream())
                    .collect(Collectors.toMap(Meta.ModelConfigDTO.ModelsDTO::getFieldName, m -> m, (o, n) -> n)).values());
            Meta.ModelConfigDTO.ModelsDTO filesDTO = CollUtil.getLast(tempModelConfigDtoList);
            filesDTO.setModels(newFileList);
            String groupKey = entry.getKey();
            groupKeyMergedFileInfoMap.put(groupKey, filesDTO);
        }

        List<Meta.ModelConfigDTO.ModelsDTO> resultList = new ArrayList<>(groupKeyMergedFileInfoMap.values());

        List<Meta.ModelConfigDTO.ModelsDTO> noGroupFileInfoList = modelsDTOList.stream()
                .filter(item -> StrUtil.isBlank(item.getFieldName())).collect(Collectors.toList());

        resultList.addAll(noGroupFileInfoList.stream()
                .collect(Collectors.toMap(
                        Meta.ModelConfigDTO.ModelsDTO::getFieldName, Function.identity(), (o, n) -> n)
                ).values());
        return resultList;
    }

}
