package cn.xiao.cg.mapper;

import cn.xiao.cg.model.entity.Generator;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 代码生成器信息 Mapper
 *
 * @author xiao
 */
public interface GeneratorMapper extends BaseMapper<Generator> {

    @Select("SELECT id, dist_path FROM t_generator WHERE is_delete = 1")
    List<Generator> listDeletedGenerator();
}




