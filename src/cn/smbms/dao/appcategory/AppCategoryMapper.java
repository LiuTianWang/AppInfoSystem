package cn.smbms.dao.appcategory;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.smbms.pojo.AppCategory;

public interface AppCategoryMapper {
	
	public List<AppCategory> getAppCategoryListByParentId(@Param("parentId")Integer parentId);
}
