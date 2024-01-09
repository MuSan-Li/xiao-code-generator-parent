package cn.xiao.cg.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.xiao.cg.common.ErrorCode;
import cn.xiao.cg.exception.BusinessException;
import cn.xiao.cg.exception.ThrowUtils;
import cn.xiao.cg.mapper.GeneratorMapper;
import cn.xiao.cg.model.dto.generator.GeneratorQueryRequest;
import cn.xiao.cg.model.entity.Generator;
import cn.xiao.cg.model.entity.User;
import cn.xiao.cg.model.vo.GeneratorVO;
import cn.xiao.cg.model.vo.UserVO;
import cn.xiao.cg.service.GeneratorService;
import cn.xiao.cg.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 帖子服务实现
 *
 * @author xiao
 */
@Service
@Slf4j
public class GeneratorServiceImpl extends ServiceImpl<GeneratorMapper, Generator> implements GeneratorService {

    @Resource
    private UserService userService;

    @Override
    public void validGenerator(Generator generator, boolean add) {
        if (Objects.isNull(generator)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = generator.getName();
        String description = generator.getDescription();

        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(name, description), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(name) && name.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "名称过长");
        }
        if (StringUtils.isNotBlank(description) && description.length() > 256) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "描述过长");
        }
    }

    /**
     * 获取查询包装类
     *
     * @param generatorQueryRequest
     * @return
     */
    @Override
    public LambdaQueryWrapper<Generator> getQueryWrapper(GeneratorQueryRequest generatorQueryRequest) {

        LambdaQueryWrapper<Generator> wrapper = Wrappers.lambdaQuery();
        if (Objects.isNull(generatorQueryRequest)) {
            return wrapper;
        }

        Long id = generatorQueryRequest.getId();
        List<String> tags = generatorQueryRequest.getTags();
        Long userId = generatorQueryRequest.getUserId();
        String name = generatorQueryRequest.getName();
        String description = generatorQueryRequest.getDescription();
        String basePackage = generatorQueryRequest.getBasePackage();
        String version = generatorQueryRequest.getVersion();
        String author = generatorQueryRequest.getAuthor();
        String distPath = generatorQueryRequest.getDistPath();
        Integer status = generatorQueryRequest.getStatus();
        String searchText = generatorQueryRequest.getSearchText();

        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            wrapper.and(qw -> qw.like(Generator::getName, searchText)
                    .or()
                    .like(Generator::getDescription, searchText));
        }

        wrapper.like(StringUtils.isNotBlank(name), Generator::getName, name);
        wrapper.like(StringUtils.isNotBlank(description), Generator::getDescription, description);
        wrapper.eq(StringUtils.isNotBlank(basePackage), Generator::getBasePackage, basePackage);
        wrapper.eq(StringUtils.isNotBlank(version), Generator::getVersion, version);
        wrapper.like(StringUtils.isNotBlank(author), Generator::getAuthor, author);
        wrapper.like(StringUtils.isNotBlank(distPath), Generator::getDistPath, distPath);
        wrapper.like(ObjectUtil.isNotEmpty(status), Generator::getStatus, status);
        if (CollUtil.isNotEmpty(tags)) {
            tags.forEach(tag -> wrapper.like(Generator::getTags, "\"" + tag + "\""));
        }
        wrapper.eq(ObjectUtils.isNotEmpty(id), Generator::getId, id);
        wrapper.eq(ObjectUtils.isNotEmpty(userId), Generator::getUserId, userId);
        return wrapper;
    }

    @Override
    public GeneratorVO getGeneratorVO(Generator generator, HttpServletRequest request) {
        GeneratorVO generatorVO = GeneratorVO.objToVo(generator);
        // 1. 关联查询用户信息
        Long userId = generator.getUserId();
        User user = null;
        if (ObjectUtil.isNotEmpty(userId) && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        generatorVO.setUser(userVO);
        return generatorVO;
    }

    @Override
    public Page<GeneratorVO> getGeneratorVOPage(Page<Generator> generatorPage, HttpServletRequest request) {

        long total = generatorPage.getTotal();
        long size = generatorPage.getSize();
        long current = generatorPage.getCurrent();

        List<Generator> generatorList = generatorPage.getRecords();
        Page<GeneratorVO> generatorVOPage = new Page<>(current, size, total);
        if (CollUtil.isEmpty(generatorList)) {
            return generatorVOPage;
        }

        // 1. 关联查询用户信息
        Set<Long> userIdSet = generatorList.stream()
                .map(Generator::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));

        // 填充信息
        List<GeneratorVO> generatorVOList = generatorList.stream()
                .map(generator -> getGeneratorVO(generator, userIdUserListMap)).collect(Collectors.toList());

        generatorVOPage.setRecords(generatorVOList);
        return generatorVOPage;
    }


    @NotNull
    private GeneratorVO getGeneratorVO(Generator generator, Map<Long, List<User>> userIdUserListMap) {
        GeneratorVO generatorVO = GeneratorVO.objToVo(generator);
        Long userId = generator.getUserId();
        User user = null;
        if (userIdUserListMap.containsKey(userId)) {
            user = userIdUserListMap.get(userId).get(0);
        }
        UserVO userVO = userService.getUserVO(user);
        if (Objects.nonNull(userVO)) {
            generatorVO.setUser(userVO);
        }
        return generatorVO;
    }

}




