package cn.xiao.maker.template;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.xiao.maker.meta.Meta;
import cn.xiao.maker.template.model.FileFilterConfig;
import cn.xiao.maker.template.model.TemplateMakerConfig;
import cn.xiao.maker.template.model.TemplateMakerFileConfig;
import cn.xiao.maker.template.model.TemplateMakerModelConfig;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TemplateMakerTest {

    // public static void main(String[] args) {
    //
    //     Meta meta = new Meta();
    //     // meta.setName("acm-template-pro-generator");
    //     // meta.setDescription("acm-示例代码生成器");
    //
    //     meta.setName("springboot-init-generator");
    //     meta.setDescription("SpringBoot-初始化项目代码生成器");
    //
    //     String projectPath = System.getProperty("user.dir");
    //     String originRootPath = new File(projectPath).getParent() + File.separator + "code-generator-template/springboot-init-template";
    //     String fileInputPath1 = "src/main/java/cn/xiao/springbootinit/common";
    //     String fileInputPath2 = "src/main/java/cn/xiao/springbootinit/controller";
    //     String fileInputPath3 = "src/main/resources";
    //
    //     TemplateMakerFileConfig templateMakerFileConfig = new TemplateMakerFileConfig();
    //     List<TemplateMakerFileConfig.FileInfoConfig> fileInfoConfigList = new ArrayList<>();
    //     TemplateMakerFileConfig.FileInfoConfig config = new TemplateMakerFileConfig.FileInfoConfig();
    //     List<FileFilterConfig> filterConfigList = new ArrayList<>();
    //     filterConfigList.add(FileFilterConfig.builder()
    //             .range(FileFilterRangeEnum.FILE_CONTENT.getValue())
    //             .value("url")
    //             .rule(FileFilterRuleEnum.CONTAINS.getValue())
    //             .build());
    //     config.setFilterConfigList(filterConfigList);
    //     config.setPath(fileInputPath3);
    //     fileInfoConfigList.add(config);
    //
    //     TemplateMakerFileConfig.FileInfoConfig config2 = new TemplateMakerFileConfig.FileInfoConfig();
    //     List<FileFilterConfig> filterConfigList2 = new ArrayList<>();
    //     filterConfigList2.add(FileFilterConfig.builder()
    //             .range(FileFilterRangeEnum.FILE_CONTENT.getValue())
    //             .value("BaseResponse")
    //             .rule(FileFilterRuleEnum.CONTAINS.getValue())
    //             .build());
    //     config2.setFilterConfigList(filterConfigList2);
    //     config2.setPath(fileInputPath1);
    //     fileInfoConfigList.add(config2);
    //     templateMakerFileConfig.setFileInfoConfigList(fileInfoConfigList);
    //
    //     // test 第一次
    //     // Meta.ModelConfigDTO.ModelsDTO modelsDTO = new Meta.ModelConfigDTO.ModelsDTO();
    //     // modelsDTO.setFieldName("outputText");
    //     // modelsDTO.setType("String");
    //     // modelsDTO.setDefaultValue("Sum: ");
    //     // String searchStr = "Sum: ";
    //
    //     // test 第二次
    //     Meta.ModelConfigDTO.ModelsDTO modelsDTO = new Meta.ModelConfigDTO.ModelsDTO();
    //     modelsDTO.setFieldName("className");
    //     modelsDTO.setType("String");
    //     modelsDTO.setDefaultValue("Sum: ");
    //
    //     // 分组配置
    //     TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = new TemplateMakerFileConfig.FileGroupConfig();
    //     fileGroupConfig.setCondition("outputText");
    //     fileGroupConfig.setGroupKey("test");
    //     fileGroupConfig.setGroupName("测试分组");
    //     templateMakerFileConfig.setFileGroupConfig(fileGroupConfig);
    //
    //     // 模型参数配置
    //     TemplateMakerModelConfig templateMakerModelConfig = new TemplateMakerModelConfig();
    //
    //     // 模型组配置
    //     TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = new TemplateMakerModelConfig.ModelGroupConfig();
    //     modelGroupConfig.setGroupKey("mysql");
    //     modelGroupConfig.setGroupName("数据库配置");
    //     templateMakerModelConfig.setModelGroupConfig(modelGroupConfig);
    //
    //     // 模型配置
    //     TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig1 = new TemplateMakerModelConfig.ModelInfoConfig();
    //     modelInfoConfig1.setFieldName("url");
    //     modelInfoConfig1.setType("String");
    //     modelInfoConfig1.setDefaultValue("jdbc:mysql://localhost:3306/my_db");
    //     modelInfoConfig1.setReplaceText("jdbc:mysql://localhost:3306/my_db");
    //     TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig2 = new TemplateMakerModelConfig.ModelInfoConfig();
    //     modelInfoConfig2.setFieldName("username");
    //     modelInfoConfig2.setType("String");
    //     modelInfoConfig2.setDefaultValue("root");
    //     modelInfoConfig2.setReplaceText("root");
    //     List<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfigList = Arrays.asList(modelInfoConfig1, modelInfoConfig2);
    //     templateMakerModelConfig.setModels(modelInfoConfigList);
    //
    //     long id = makeTemplate(meta, originRootPath, templateMakerFileConfig, templateMakerModelConfig, 1L);
    //     System.out.println(id);
    //     System.out.println("============= success =============");
    // }

    /**
     * 测试 Bug-1
     */
    @Test
    public void testMakeTemplateBug1() throws Exception {

        Meta meta = new Meta();
        meta.setName("springboot-init-generator");
        meta.setDescription("SpringBoot-初始化项目代码生成器");

        String projectPath = System.getProperty("user.dir");
        String originRootPath = new File(projectPath).getParent() + File.separator + "code-generator-template/springboot-init-template";


        TemplateMakerFileConfig templateMakerFileConfig = new TemplateMakerFileConfig();

        List<TemplateMakerFileConfig.FileInfoConfig> fileInfoConfigList = new ArrayList<>();

        TemplateMakerFileConfig.FileInfoConfig config = new TemplateMakerFileConfig.FileInfoConfig();

        List<FileFilterConfig> filterConfigList = new ArrayList<>();

        filterConfigList.add(FileFilterConfig.builder().build());

        String fileInputPath = "src/main/resources/application-dev.yml";
        config.setFilterConfigList(filterConfigList);
        config.setPath(fileInputPath);

        fileInfoConfigList.add(config);
        templateMakerFileConfig.setFiles(fileInfoConfigList);

        // 模型参数配置
        TemplateMakerModelConfig templateMakerModelConfig = new TemplateMakerModelConfig();
        // 模型配置
        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig.setFieldName("url");
        modelInfoConfig.setType("String");
        modelInfoConfig.setDefaultValue("jdbc:mysql://localhost:3306/my_db");
        modelInfoConfig.setReplaceText("jdbc:mysql://localhost:3306/my_db");

        templateMakerModelConfig.setModels(Collections.singletonList(modelInfoConfig));

        long id = TemplateMaker.makeTemplate(meta, originRootPath, templateMakerFileConfig, templateMakerModelConfig, null, 1L);
        System.out.println("id = " + id);
        System.out.println("============= success =============");
    }


    /**
     * 测试 Bug-2
     */
    @Test
    public void testMakeTemplateBug2() {
        Meta meta = new Meta();
        meta.setName("springboot-init-generator");
        meta.setDescription("SpringBoot-初始化项目代码生成器");
        String projectPath = System.getProperty("user.dir");
        String originProjectPath = new File(projectPath).getParent() + File.separator + "code-generator-template/springboot-init-template";
        // 文件参数配置，扫描目录
        String inputFilePath = "./";
        TemplateMakerFileConfig templateMakerFileConfig = new TemplateMakerFileConfig();
        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig1 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig1.setPath(inputFilePath);
        templateMakerFileConfig.setFiles(Collections.singletonList(fileInfoConfig1));
        // 模型参数配置
        TemplateMakerModelConfig templateMakerModelConfig = new TemplateMakerModelConfig();
        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig.setFieldName("className");
        modelInfoConfig.setType("String");
        modelInfoConfig.setReplaceText("BaseResponse");
        List<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfigList = Collections.singletonList(modelInfoConfig);
        templateMakerModelConfig.setModels(modelInfoConfigList);
        long id = TemplateMaker.makeTemplate(meta, originProjectPath, templateMakerFileConfig, templateMakerModelConfig, null, 1735281524670181376L);
        System.out.println(id);
    }

    /**
     * 测试制作springboot模板
     */
    @Test
    public void testMakeTemplate() {
        String templateMakerJsonStr = ResourceUtil.readUtf8Str("templateMaker.json");
        TemplateMakerConfig templateMakerConfig = JSONUtil.toBean(templateMakerJsonStr, TemplateMakerConfig.class);
        long id = TemplateMaker.makeTemplate(templateMakerConfig);
        System.out.println(id);
    }

    /**
     * 测试制作springboot模板
     */
    @Test
    public void testMakeSpringBootInitTemplate() {
        String rootPath = "examples/springboot-init/";
        String templateMakerJsonStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker.json");
        TemplateMakerConfig templateMakerConfig = JSONUtil.toBean(templateMakerJsonStr, TemplateMakerConfig.class);
        long id = TemplateMaker.makeTemplate(templateMakerConfig);
        System.out.println(id);
        int index = 1;
        while (true) {
            try {
                templateMakerJsonStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker-" + index + ".json");
                System.out.println("index = " + index);
                templateMakerConfig = JSONUtil.toBean(templateMakerJsonStr, TemplateMakerConfig.class);
                TemplateMaker.makeTemplate(templateMakerConfig);
                index++;
            } catch (Exception e) {
                System.out.println("生成模板失败，请检查模板配置");
                return;
            }
        }
    }
}