package cn.xiao.maker.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.xiao.maker.meta.Meta;
import cn.xiao.maker.meta.enums.FileGenerateTypeEnum;
import cn.xiao.maker.meta.enums.FileTypeEnum;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
        meta.setName("acm-template-pro-generator-v2");
        meta.setDescription("acm-示例代码生成器-v2");

        String projectPath = System.getProperty("user.dir");
        String originRootPath = new File(projectPath).getParent() + File.separator + "code-generator-template/acm-template";
        String fileInputPath = "src/main/java/cn/xiao/MainTemplate.java";

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
        String searchStr = "MainTemplate";

        long id = makeTemplate(meta, originRootPath, fileInputPath, modelsDTO, searchStr, 1L);
        System.out.println(id);

        System.out.println("============= success =============");
    }

    private static long makeTemplate(Meta newMeta, String originRootPath, String fileInputPath,
                                     Meta.ModelConfigDTO.ModelsDTO modelsDTO, String searchStr, Long id) {
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
        String templateDirPath = tempDirPath + File.separator + originLastFileName + "-" + id;
        if (!FileUtil.exist(templateDirPath)) {
            FileUtil.mkdir(templateDirPath);
            FileUtil.copy(originRootPath, templateDirPath, true);
        }

        // win 路径转义
        String sourceRootPath = templateDirPath + File.separator + originLastFileName;
        String fileOutputPath = fileInputPath + ".ftl";

        // 1.3文件参数信息
        Meta.FileConfigDTO.FilesDTO filesDTO = new Meta.FileConfigDTO.FilesDTO();
        filesDTO.setInputPath(fileInputPath);
        filesDTO.setOutputPath(fileOutputPath);
        filesDTO.setType(FileTypeEnum.FILE.getValue());
        filesDTO.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());

        // 2使用字符串替换 生成模板文件
        String fileInputAbsolutePath = sourceRootPath + File.separator + fileInputPath;
        String fileOutputAbsolutePath = sourceRootPath + File.separator + fileOutputPath;

        boolean existFile = FileUtil.exist(fileOutputAbsolutePath);
        String fileContent = FileUtil.readUtf8String(existFile ? fileOutputAbsolutePath : fileInputAbsolutePath);
        String replacement = String.format("${%s}", modelsDTO.getFieldName());
        String newFileContent = StrUtil.replace(fileContent, searchStr, replacement);
        FileUtil.writeUtf8String(newFileContent, fileOutputAbsolutePath);

        // 3生成配置文件
        String metaOutPath = sourceRootPath + File.separator + "meta.json";

        if (FileUtil.exist(metaOutPath)) {
            Meta oldMetaInfo = JSONUtil.toBean(FileUtil.readUtf8String(metaOutPath), Meta.class);
            BeanUtil.copyProperties(newMeta, oldMetaInfo, CopyOptions.create().ignoreNullValue());
            newMeta = oldMetaInfo;

            List<Meta.FileConfigDTO.FilesDTO> filesDTOList = newMeta.getFileConfig().getFiles();
            filesDTOList.add(filesDTO);
            List<Meta.ModelConfigDTO.ModelsDTO> modelsDTOList = newMeta.getModelConfig().getModels();
            modelsDTOList.add(modelsDTO);
            // 去重配置
            newMeta.getFileConfig().setFiles(distinctFile(filesDTOList));
            newMeta.getModelConfig().setModels(distinctModel(modelsDTOList));

            FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newMeta), metaOutPath);
            return id;
        }
        Meta.FileConfigDTO fileConfigDTO = new Meta.FileConfigDTO();
        fileConfigDTO.setSourceRootPath(sourceRootPath);

        // 3.1构造file配置参数
        List<Meta.FileConfigDTO.FilesDTO> files = new ArrayList<>();
        files.add(filesDTO);
        fileConfigDTO.setFiles(files);
        newMeta.setFileConfig(fileConfigDTO);

        // 3.2构造model配置参数
        Meta.ModelConfigDTO modelConfigDTO = new Meta.ModelConfigDTO();
        List<Meta.ModelConfigDTO.ModelsDTO> models = new ArrayList<>();
        modelConfigDTO.setModels(models);
        models.add(modelsDTO);
        newMeta.setModelConfig(modelConfigDTO);

        // 3.3输入元信息文件
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newMeta), metaOutPath);

        System.out.println("============= success =============");
        return id;
    }

    /**
     * 去重文件
     *
     * @return
     */
    private static List<Meta.FileConfigDTO.FilesDTO> distinctFile(List<Meta.FileConfigDTO.FilesDTO> filesDTOList) {
        return new ArrayList<>(filesDTOList.stream()
                .collect(Collectors.toMap(
                        Meta.FileConfigDTO.FilesDTO::getInputPath, Function.identity(), (o, n) -> n)
                ).values());
    }

    /**
     * 去重模型
     *
     * @return
     */
    private static List<Meta.ModelConfigDTO.ModelsDTO> distinctModel(List<Meta.ModelConfigDTO.ModelsDTO> modelsDTOList) {
        return new ArrayList<>(modelsDTOList.stream()
                .collect(Collectors.toMap(
                        Meta.ModelConfigDTO.ModelsDTO::getFieldName, Function.identity(), (o, n) -> n)
                ).values());
    }

}
