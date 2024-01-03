package cn.xiao.maker.template;

import cn.hutool.core.text.CharSequenceUtil;
import cn.xiao.maker.meta.Meta;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 模板制作工具类
 *
 * @author xiao
 */
public class TemplateMakerUtils {

    private TemplateMakerUtils() {
    }

    /**
     * 从未分组文件中移除组内的同名文件
     *
     * @param fileInfoList
     * @return
     */
    public static List<Meta.FileConfigDTO.FilesDTO> removeGroupFilesFormRoot(
            List<Meta.FileConfigDTO.FilesDTO> fileInfoList) {

        // 获取所有分组
        List<Meta.FileConfigDTO.FilesDTO> groupFileInfoList = fileInfoList.stream()
                .filter(item -> CharSequenceUtil.isNotBlank(item.getGroupKey()))
                .collect(Collectors.toList());

        // 获取分组内的文件集合
        List<Meta.FileConfigDTO.FilesDTO> groupInnerFileInfoList = groupFileInfoList.stream()
                .flatMap(item -> item.getFiles().stream())
                .collect(Collectors.toList());

        // 获取输入路径集合
        Set<String> inputPathSet = groupInnerFileInfoList.stream()
                .map(Meta.FileConfigDTO.FilesDTO::getInputPath)
                .collect(Collectors.toSet());

        // 移除所有在外层集合中的 输入路径集合
        return fileInfoList.stream()
                .filter(item -> !inputPathSet.contains(item.getInputPath()))
                .collect(Collectors.toList());
    }
}
