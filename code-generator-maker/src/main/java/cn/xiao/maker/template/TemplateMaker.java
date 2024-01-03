package cn.xiao.maker.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import cn.xiao.maker.constant.CommonConstant;
import cn.xiao.maker.meta.Meta;
import cn.xiao.maker.meta.enums.FileGenerateTypeEnum;
import cn.xiao.maker.meta.enums.FileTypeEnum;
import cn.xiao.maker.template.model.TemplateMakerConfig;
import cn.xiao.maker.template.model.TemplateMakerFileConfig;
import cn.xiao.maker.template.model.TemplateMakerModelConfig;
import cn.xiao.maker.template.model.TemplateMakerOutputConfig;

import java.io.File;
import java.util.ArrayList;
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


    /**
     * 模板制作
     *
     * @param templateMakerConfig
     * @return
     */
    public static long makeTemplate(TemplateMakerConfig templateMakerConfig) {
        Long id = templateMakerConfig.getId();
        Meta meta = templateMakerConfig.getMeta();
        String originProjectPath = templateMakerConfig.getOriginProjectPath();
        TemplateMakerFileConfig fileConfig = templateMakerConfig.getFileConfig();
        TemplateMakerModelConfig modelConfig = templateMakerConfig.getModelConfig();
        TemplateMakerOutputConfig outputConfig = templateMakerConfig.getOutputConfig();
        return makeTemplate(meta, originProjectPath, fileConfig, modelConfig, outputConfig, id);
    }

    /**
     * 模板制作
     *
     * @param newMeta
     * @param originRootPath
     * @param fileConfig
     * @param modelConfig
     * @param outputConfig
     * @param id
     * @return
     */
    public static long makeTemplate(Meta newMeta, String originRootPath, TemplateMakerFileConfig fileConfig,
                                    TemplateMakerModelConfig modelConfig, TemplateMakerOutputConfig outputConfig,
                                    Long id) {
        // id为空则直生成
        if (Objects.isNull(id)) {
            id = IdUtil.getSnowflakeNextId();
        }

        // 1.输入信息
        // 1.1输入文件信息
        String projectPath = System.getProperty(CommonConstant.USER_DIR);

        // 1.2复制原始模板到工作空间
        // String templateDirPath = tempDirPath + File.separator + originLastFileName + StrPool.DASHED + id;
        String templateDirPath = projectPath + File.separator + CommonConstant.WORK_SPACE + File.separator + id;
        if (!FileUtil.exist(templateDirPath)) {
            FileUtil.mkdir(templateDirPath);
            FileUtil.copy(originRootPath, templateDirPath, true);
        }

        //源模版项目根目录
        // 处理多次输入 originRootPath 为空场景
        String sourceRootPath = FileUtil.loopFiles(new File(templateDirPath), 1, null)
                .stream()
                .filter(File::isDirectory)
                .findFirst()
                .orElseThrow(RuntimeException::new)
                .getAbsolutePath();

        // win 路径转义
        sourceRootPath = sourceRootPath.replaceAll(CommonConstant.BACK_SLASH, StrPool.SLASH);

        // 处理文件信息
        List<Meta.FileConfigDTO.FilesDTO> newFilesDTOList = makeFileTemplates(fileConfig,
                modelConfig, sourceRootPath);

        //

        // 处理模型信息
        List<Meta.ModelConfigDTO.ModelsDTO> newModelsDTOList = getModelInfoList(modelConfig);

        // 生成配置文件
        String metaOutPath = templateDirPath + File.separator + CommonConstant.META_NAME;

        if (FileUtil.exist(metaOutPath)) {
            newMeta = JSONUtil.toBean(FileUtil.readUtf8String(metaOutPath), Meta.class);

            List<Meta.FileConfigDTO.FilesDTO> filesDTOList = newMeta.getFileConfig().getFiles();
            filesDTOList.addAll(newFilesDTOList);
            List<Meta.ModelConfigDTO.ModelsDTO> modelsDTOList = newMeta.getModelConfig().getModels();
            modelsDTOList.addAll(newModelsDTOList);

            // 去重配置
            newMeta.getFileConfig().setFiles(distinctFile(filesDTOList));
            newMeta.getModelConfig().setModels(distinctModel(modelsDTOList));
        } else {
            Meta.FileConfigDTO fileConfigDTO = new Meta.FileConfigDTO();
            fileConfigDTO.setSourceRootPath(sourceRootPath);

            // 3.1构造file配置参数
            fileConfigDTO.setFiles(newFilesDTOList);
            newMeta.setFileConfig(fileConfigDTO);

            // 3.2构造model配置参数
            Meta.ModelConfigDTO modelConfigDTO = new Meta.ModelConfigDTO();
            modelConfigDTO.setModels(newModelsDTOList);
            newMeta.setModelConfig(modelConfigDTO);
        }

        removeFileFormRoot(outputConfig, newMeta);

        // 3.3创建元信息文件
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newMeta), metaOutPath);
        return id;
    }

    /**
     * 从未分组文件中移除组内的同名文件
     *
     * @param outputConfig
     * @param newMeta
     */
    private static void removeFileFormRoot(TemplateMakerOutputConfig outputConfig, Meta newMeta) {
        if (Objects.isNull(outputConfig)) {
            return;
        }
        if (!outputConfig.isRemoveGroupFilesFromRoot()) {
            return;
        }
        List<Meta.FileConfigDTO.FilesDTO> filesDTOList = newMeta.getFileConfig().getFiles();
        newMeta.getFileConfig().setFiles(TemplateMakerUtils.removeGroupFilesFormRoot(filesDTOList));
    }

    /**
     * 处理模型信息
     *
     * @param modelConfig
     * @return
     */
    private static List<Meta.ModelConfigDTO.ModelsDTO> getModelInfoList(TemplateMakerModelConfig modelConfig) {

        // 结果集合
        List<Meta.ModelConfigDTO.ModelsDTO> newModelsDTOList = new ArrayList<>();
        if (Objects.isNull(modelConfig)) {
            return newModelsDTOList;
        }
        List<TemplateMakerModelConfig.ModelInfoConfig> models = modelConfig.getModels();

        if (CollUtil.isEmpty(models)) {
            return newModelsDTOList;
        }

        //  对象转换 ModelInfoConfig => ModelsDTO
        List<Meta.ModelConfigDTO.ModelsDTO> inputModelsDTOList = models.stream().map(item -> {
            Meta.ModelConfigDTO.ModelsDTO modelsDTO = new Meta.ModelConfigDTO.ModelsDTO();
            BeanUtil.copyProperties(item, modelsDTO);
            return modelsDTO;
        }).collect(Collectors.toList());


        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = modelConfig.getModelGroupConfig();

        // 如果是模型组 全放到一个分组内
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
        return newModelsDTOList;
    }

    /**
     * 处理文件信息
     *
     * @param fileConfig
     * @param modelConfig
     * @param sourceRootPath
     * @return
     */
    private static List<Meta.FileConfigDTO.FilesDTO> makeFileTemplates(TemplateMakerFileConfig fileConfig,
                                                                       TemplateMakerModelConfig modelConfig,
                                                                       String sourceRootPath) {

        List<Meta.FileConfigDTO.FilesDTO> newFilesDTOList = new ArrayList<>();

        if (Objects.isNull(fileConfig)) {
            return newFilesDTOList;
        }

        List<TemplateMakerFileConfig.FileInfoConfig> fileList = fileConfig.getFiles();

        if (CollUtil.isEmpty(fileList)) {
            return newFilesDTOList;
        }

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
            files = files.stream()
                    .filter(item -> !item.getAbsolutePath().endsWith(CommonConstant.MODEL_END_WITH))
                    .collect(Collectors.toList());
            for (File file : files) {
                newFilesDTOList.add(makeFileTemplate(sourceRootPath, file, modelConfig));
            }
        }
        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = fileConfig.getFileGroupConfig();

        if (Objects.nonNull(fileGroupConfig)) {
            Meta.FileConfigDTO.FilesDTO fileConfigDTO = getFilesDTO(fileGroupConfig, newFilesDTOList);
            newFilesDTOList = new ArrayList<>();
            newFilesDTOList.add(fileConfigDTO);
        }
        return newFilesDTOList;
    }

    /**
     * 处理file group
     *
     * @param fileGroupConfig
     * @param newFilesDTOList
     * @return
     */
    private static Meta.FileConfigDTO.FilesDTO getFilesDTO(TemplateMakerFileConfig.FileGroupConfig fileGroupConfig,
                                                           List<Meta.FileConfigDTO.FilesDTO> newFilesDTOList) {
        String condition = fileGroupConfig.getCondition();
        String groupKey = fileGroupConfig.getGroupKey();
        String groupName = fileGroupConfig.getGroupName();
        Meta.FileConfigDTO.FilesDTO fileConfigDTO = new Meta.FileConfigDTO.FilesDTO();
        fileConfigDTO.setType(FileTypeEnum.GROUP.getValue());
        fileConfigDTO.setCondition(condition);
        fileConfigDTO.setGroupKey(groupKey);
        fileConfigDTO.setGroupName(groupName);
        fileConfigDTO.setFiles(newFilesDTOList);
        return fileConfigDTO;
    }

    /**
     * 模板文件
     *
     * @param sourceRootPath
     * @param inputFile
     * @param modelConfig
     * @return
     */
    private static Meta.FileConfigDTO.FilesDTO makeFileTemplate(String sourceRootPath, File inputFile,
                                                                TemplateMakerModelConfig modelConfig) {
        // 举例：D:\Project\xiao-code-generator-parent\code-generator-maker\src\main\java\cn\xiao\xxx
        // win系统对路径转义
        String fileInputAbsolutePath = inputFile.getAbsolutePath().replaceAll(CommonConstant.BACK_SLASH, StrPool.SLASH);
        String fileOutputAbsolutePath = fileInputAbsolutePath + CommonConstant.MODEL_END_WITH;

        // 举例：src/main/java/cn/xiao/xxx
        // 文件输入输出替换为相对路径
        String fileInputPath = fileInputAbsolutePath.replace(sourceRootPath + StrPool.SLASH, CommonConstant.BLANK);
        String fileOutputPath = fileInputPath + CommonConstant.MODEL_END_WITH;

        boolean existFile = FileUtil.exist(fileOutputAbsolutePath);
        String fileContent = FileUtil.readUtf8String(existFile ? fileOutputAbsolutePath : fileInputAbsolutePath);
        String replacement;
        String newFileContent = fileContent;
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = modelConfig.getModelGroupConfig();
        List<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfigList = modelConfig.getModels();

        for (TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig : modelInfoConfigList) {
            // 不是分组
            if (Objects.isNull(modelGroupConfig)) {
                replacement = String.format(CommonConstant.FORMAT_FIELD_NAME, modelInfoConfig.getFieldName());
            } else {
                // 注意挖坑要多一个层级
                replacement = String.format(CommonConstant.FORMAT_KV_FIELD_NAME, modelGroupConfig.getGroupKey(),
                        modelInfoConfig.getFieldName());
            }
            // 多次替换
            newFileContent = CharSequenceUtil.replace(newFileContent, modelInfoConfig.getReplaceText(), replacement);
        }


        // 文件参数信息
        Meta.FileConfigDTO.FilesDTO filesDTO = new Meta.FileConfigDTO.FilesDTO();
        filesDTO.setInputPath(fileOutputPath);
        filesDTO.setOutputPath(fileInputPath);
        filesDTO.setType(FileTypeEnum.FILE.getValue());
        filesDTO.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());

        boolean contentEquals = Objects.equals(fileContent, newFileContent);
        // 之前不存在模板文件，并且没有更改文件内容，则为静态生成
        if (!existFile) {
            // 修改的文件内容和原文件一样 为静态文件否则动态文件
            if (contentEquals) {
                filesDTO.setInputPath(fileInputPath);
                filesDTO.setGenerateType(FileGenerateTypeEnum.STATIC.getValue());
            } else {
                FileUtil.writeUtf8String(newFileContent, fileOutputAbsolutePath);
            }
        } else if (!contentEquals) {
            // 有模板文件，且增加了新坑，生成模板文件
            FileUtil.writeUtf8String(newFileContent, fileOutputAbsolutePath);
        }
        return filesDTO;

    }

    /**
     * 去重文件
     *
     * @return
     */
    private static List<Meta.FileConfigDTO.FilesDTO> distinctFile(List<Meta.FileConfigDTO.FilesDTO> filesDTOList) {
        // 策略：同分组内文件 merge，不同分组保留
        Map<String, List<Meta.FileConfigDTO.FilesDTO>> groupKeyFileInfoListMap = filesDTOList.stream()
                .filter(item -> CharSequenceUtil.isNotBlank(item.getGroupKey()))
                .collect(Collectors.groupingBy(Meta.FileConfigDTO.FilesDTO::getGroupKey));

        // 2. 同组内的文件配置合并
        // 保存每个组对应的合并后的对象 map
        Map<String, Meta.FileConfigDTO.FilesDTO> groupKeyMergedFileInfoMap = new HashMap<>();
        for (Map.Entry<String, List<Meta.FileConfigDTO.FilesDTO>> entry : groupKeyFileInfoListMap.entrySet()) {

            List<Meta.FileConfigDTO.FilesDTO> tempFileConfigDtoList = entry.getValue();
            List<Meta.FileConfigDTO.FilesDTO> newFileList = new ArrayList<>(tempFileConfigDtoList.stream()
                    .flatMap(item -> item.getFiles().stream())
                    .collect(Collectors.toMap(Meta.FileConfigDTO.FilesDTO::getOutputPath,
                            m -> m, (o, n) -> n)).values());
            Meta.FileConfigDTO.FilesDTO filesDTO = CollUtil.getLast(tempFileConfigDtoList);
            filesDTO.setFiles(newFileList);
            String groupKey = entry.getKey();
            groupKeyMergedFileInfoMap.put(groupKey, filesDTO);
        }

        List<Meta.FileConfigDTO.FilesDTO> resultList = new ArrayList<>(groupKeyMergedFileInfoMap.values());

        List<Meta.FileConfigDTO.FilesDTO> noGroupFileInfoList = filesDTOList.stream()
                .filter(item -> CharSequenceUtil.isBlank(item.getGroupKey())).collect(Collectors.toList());

        resultList.addAll(noGroupFileInfoList.stream()
                .collect(Collectors.toMap(
                        Meta.FileConfigDTO.FilesDTO::getOutputPath, Function.identity(), (o, n) -> n)
                ).values());
        return resultList;
    }

    /**
     * 去重模型
     *
     * @return
     */
    private static List<Meta.ModelConfigDTO.ModelsDTO> distinctModel(List<Meta.ModelConfigDTO.ModelsDTO> modelsDTOS) {
        // 策略：同分组内文件 merge，不同分组保留
        Map<String, List<Meta.ModelConfigDTO.ModelsDTO>> groupKeyFileInfoListMap = modelsDTOS.stream()
                .filter(item -> CharSequenceUtil.isNotBlank(item.getGroupKey()))
                .collect(Collectors.groupingBy(Meta.ModelConfigDTO.ModelsDTO::getGroupKey));

        // 2. 同组内的模型配置合并
        // 保存每个组对应的合并后的对象 map
        Map<String, Meta.ModelConfigDTO.ModelsDTO> groupKeyMergedFileInfoMap = new HashMap<>();
        for (Map.Entry<String, List<Meta.ModelConfigDTO.ModelsDTO>> entry : groupKeyFileInfoListMap.entrySet()) {

            List<Meta.ModelConfigDTO.ModelsDTO> tempModelConfigDtoList = entry.getValue();
            List<Meta.ModelConfigDTO.ModelsDTO> newFileList = new ArrayList<>(tempModelConfigDtoList.stream()
                    .flatMap(item -> item.getModels().stream())
                    .collect(Collectors.toMap(Meta.ModelConfigDTO.ModelsDTO::getFieldName,
                            m -> m, (o, n) -> n)).values());
            Meta.ModelConfigDTO.ModelsDTO filesDTO = CollUtil.getLast(tempModelConfigDtoList);
            filesDTO.setModels(newFileList);
            String groupKey = entry.getKey();
            groupKeyMergedFileInfoMap.put(groupKey, filesDTO);
        }

        List<Meta.ModelConfigDTO.ModelsDTO> resultList = new ArrayList<>(groupKeyMergedFileInfoMap.values());

        List<Meta.ModelConfigDTO.ModelsDTO> noGroupFileInfoList = modelsDTOS.stream()
                .filter(item -> CharSequenceUtil.isBlank(item.getGroupKey())).collect(Collectors.toList());

        resultList.addAll(noGroupFileInfoList.stream()
                .collect(Collectors.toMap(
                        Meta.ModelConfigDTO.ModelsDTO::getFieldName, Function.identity(), (o, n) -> n)
                ).values());
        return resultList;
    }

}
