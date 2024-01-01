package cn.xiao.maker.meta;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.PathUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.xiao.maker.meta.enums.FileGenerateTypeEnum;
import cn.xiao.maker.meta.enums.FileTypeEnum;
import cn.xiao.maker.meta.enums.ModelTypeEnum;

import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Meta 校验器
 *
 * @author xiao
 */
public class MetaValidator {

    private MetaValidator() {
    }

    public static void doValidaAndFill(Meta meta) {
        validAndFillMetaRoot(meta);
        validAndFillFileConfig(meta);
        validAndFillModelConfig(meta);
    }

    private static void validAndFillModelConfig(Meta meta) {
        // modelConfig 校验和默认值
        Meta.ModelConfigDTO modelConfig = meta.getModelConfig();
        if (ObjectUtil.isEmpty(modelConfig)) {
            return;
        }
        List<Meta.ModelConfigDTO.ModelsDTO> modelInfoList = modelConfig.getModels();
        if (CollUtil.isEmpty(modelInfoList)) {
            return;
        }
        for (Meta.ModelConfigDTO.ModelsDTO modelInfo : modelInfoList) {
            if (CharSequenceUtil.isNotEmpty(modelInfo.getGroupKey())) {
                // 生成中间参数
                List<Meta.ModelConfigDTO.ModelsDTO> subModelInfoList = modelInfo.getModels();
                String allArgsStr = modelInfo.getModels().stream()
                        .map(subModelInfo -> String.format("\"--%s\"", subModelInfo.getFieldName()))
                        .collect(Collectors.joining(", "));
                modelInfo.setAllArgsStr(allArgsStr);
                continue;
            }
            // 输出路径默认值
            if (CharSequenceUtil.isBlank(modelInfo.getFieldName())) {
                throw new MetaException("未填写 fieldName");
            }
            modelInfo.setType(CharSequenceUtil.emptyToDefault(modelInfo.getType(), ModelTypeEnum.STRING.getValue()));
        }
    }

    private static void validAndFillFileConfig(Meta meta) {
        // fileConfig 校验和默认值
        Meta.FileConfigDTO fileConfig = meta.getFileConfig();
        if (fileConfig == null) {
            return;
        }
        // sourceRootPath 必填
        String sourceRootPath = fileConfig.getSourceRootPath();
        if (CharSequenceUtil.isBlank(sourceRootPath)) {
            throw new MetaException("未填写 sourceRootPath");
        }
        // inputRootPath: .source + sourceRootPath 的最后一个层级路径
        String inputRootPath = fileConfig.getInputRootPath();
        String defaultInputRootPath = ".source/" + PathUtil.getLastPathEle(Paths.get(sourceRootPath)).getFileName().toString();
        if (CharSequenceUtil.isEmpty(inputRootPath)) {
            fileConfig.setInputRootPath(defaultInputRootPath);
        }
        // outputRootPath: 默认为当前路径下的 generated
        String outputRootPath = fileConfig.getOutputRootPath();
        String defaultOutputRootPath = "generated";
        if (CharSequenceUtil.isEmpty(outputRootPath)) {
            fileConfig.setOutputRootPath(defaultOutputRootPath);
        }
        String fileConfigType = fileConfig.getType();
        String defaultType = FileTypeEnum.DIR.getValue();
        if (CharSequenceUtil.isEmpty(fileConfigType)) {
            fileConfig.setType(defaultType);
        }

        // fileInfo 默认值
        List<Meta.FileConfigDTO.FilesDTO> fileInfoList = fileConfig.getFiles();
        if (CollUtil.isEmpty(fileInfoList)) {
            return;
        }
        for (Meta.FileConfigDTO.FilesDTO fileInfo : fileInfoList) {
            // 分组不为空 跳过
            if (FileTypeEnum.GROUP.getValue().equals(fileInfo.getType())) {
                continue;
            }
            // inputPath 必填
            String inputPath = fileInfo.getInputPath();
            if (CharSequenceUtil.isBlank(inputPath)) {
                throw new MetaException("未填写 inputPath");
            }
            // outputPath 默认等于 inputPath
            String outputPath = fileInfo.getOutputPath();
            if (CharSequenceUtil.isEmpty(outputPath)) {
                fileInfo.setOutputPath(inputPath);
            }

            // type: 默认 inputPath 有文件后缀（如.java）为 file，否则为 dir
            String type = fileInfo.getType();
            if (CharSequenceUtil.isBlank(type)) {
                // 无文件后缀
                fileInfo.setType(CharSequenceUtil.isBlank(FileUtil.getSuffix(inputPath)) ? FileTypeEnum.DIR.getValue() : FileTypeEnum.FILE.getValue());
            }

            // generateType: 如果文件结尾不为 .ftl，默认为static，否则为 dynamic
            String generateType = fileInfo.getGenerateType();
            if (CharSequenceUtil.isBlank(generateType)) {
                // 动态模板
                fileInfo.setGenerateType(inputPath.endsWith(".ftl") ? FileGenerateTypeEnum.DYNAMIC.getValue() : FileGenerateTypeEnum.STATIC.getValue());
            }
        }
    }

    private static void validAndFillMetaRoot(Meta meta) {
        // 基础信息校验和默认值
        meta.setName(CharSequenceUtil.blankToDefault(meta.getName(), "my-generator"));
        meta.setDescription(CharSequenceUtil.emptyToDefault(meta.getDescription(), "我的模板代码生成器"));
        meta.setBasePackage(CharSequenceUtil.blankToDefault(meta.getBasePackage(), "cn.xiao"));
        meta.setVersion(CharSequenceUtil.emptyToDefault(meta.getVersion(), "1.0"));
        meta.setAuthor(CharSequenceUtil.emptyToDefault(meta.getAuthor(), "xiao"));
        meta.setCreateTime(CharSequenceUtil.emptyToDefault(meta.getCreateTime(), DateUtil.now()));
    }
}