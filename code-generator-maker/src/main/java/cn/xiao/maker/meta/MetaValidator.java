package cn.xiao.maker.meta;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.xiao.maker.meta.enums.FileGenerateTypeEnum;
import cn.xiao.maker.meta.enums.FileTypeEnum;
import cn.xiao.maker.meta.enums.ModelTypeEnum;

import java.nio.file.Paths;
import java.util.List;

/**
 * Meta 校验器
 *
 * @author xiao
 */
public class MetaValidator {

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
        if (CollectionUtil.isEmpty(modelInfoList)) {
            return;
        }
        for (Meta.ModelConfigDTO.ModelsDTO modelInfo : modelInfoList) {
            // 输出路径默认值
            if (CharSequenceUtil.isBlank(modelInfo.getFieldName())) {
                throw new MetaException("未填写 fieldName");
            }
            modelInfo.setType(StrUtil.emptyToDefault(modelInfo.getType(), ModelTypeEnum.STRING.getValue()));
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
        if (StrUtil.isBlank(sourceRootPath)) {
            throw new MetaException("未填写 sourceRootPath");
        }
        // inputRootPath: .source + sourceRootPath 的最后一个层级路径
        String inputRootPath = fileConfig.getInputRootPath();
        String defaultInputRootPath = ".source/" + FileUtil.getLastPathEle(Paths.get(sourceRootPath)).getFileName().toString();
        if (StrUtil.isEmpty(inputRootPath)) {
            fileConfig.setInputRootPath(defaultInputRootPath);
        }
        // outputRootPath: 默认为当前路径下的 generated
        String outputRootPath = fileConfig.getOutputRootPath();
        String defaultOutputRootPath = "generated";
        if (StrUtil.isEmpty(outputRootPath)) {
            fileConfig.setOutputRootPath(defaultOutputRootPath);
        }
        String fileConfigType = fileConfig.getType();
        String defaultType = FileTypeEnum.DIR.getValue();
        if (StrUtil.isEmpty(fileConfigType)) {
            fileConfig.setType(defaultType);
        }

        // fileInfo 默认值
        List<Meta.FileConfigDTO.FilesDTO> fileInfoList = fileConfig.getFiles();
        if (!CollectionUtil.isNotEmpty(fileInfoList)) {
            return;
        }
        for (Meta.FileConfigDTO.FilesDTO fileInfo : fileInfoList) {
            // 分组不为空 跳过
            if (FileTypeEnum.GROUP.getValue().equals(fileInfo.getType())) {
                continue;
            }
            // inputPath 必填
            String inputPath = fileInfo.getInputPath();
            if (StrUtil.isBlank(inputPath)) {
                throw new MetaException("未填写 inputPath");
            }
            // outputPath 默认等于 inputPath
            String outputPath = fileInfo.getOutputPath();
            if (StrUtil.isEmpty(outputPath)) {
                fileInfo.setOutputPath(inputPath);
            }

            // type: 默认 inputPath 有文件后缀（如.java）为 file，否则为 dir
            String type = fileInfo.getType();
            if (StrUtil.isBlank(type)) {
                // 无文件后缀
                fileInfo.setType(StrUtil.isBlank(FileUtil.getSuffix(inputPath)) ? FileTypeEnum.DIR.getValue() : FileTypeEnum.FILE.getValue());
            }

            // generateType: 如果文件结尾不为 .ftl，默认为static，否则为 dynamic
            String generateType = fileInfo.getGenerateType();
            if (StrUtil.isBlank(generateType)) {
                // 动态模板
                fileInfo.setGenerateType(inputPath.endsWith(".ftl") ? FileGenerateTypeEnum.DYNAMIC.getValue() : FileGenerateTypeEnum.STATIC.getValue());
            }
        }
    }

    private static void validAndFillMetaRoot(Meta meta) {
        // 基础信息校验和默认值
        meta.setName(StrUtil.blankToDefault(meta.getName(), "my-generator"));
        meta.setDescription(StrUtil.emptyToDefault(meta.getDescription(), "我的模板代码生成器"));
        meta.setBasePackage(StrUtil.blankToDefault(meta.getBasePackage(), "cn.xiao"));
        meta.setVersion(StrUtil.emptyToDefault(meta.getVersion(), "1.0"));
        meta.setAuthor(StrUtil.emptyToDefault(meta.getAuthor(), "xiao"));
        meta.setCreateTime(StrUtil.emptyToDefault(meta.getCreateTime(), DateUtil.now()));
    }
}