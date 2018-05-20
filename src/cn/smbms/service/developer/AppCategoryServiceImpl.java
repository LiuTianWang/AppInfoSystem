package cn.smbms.service.developer;

import java.util.List;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.smbms.dao.appcategory.AppCategoryMapper;
import cn.smbms.pojo.AppCategory;
@Service
public class AppCategoryServiceImpl implements AppCategoryService {

	@Resource
	private AppCategoryMapper mapper;
	
	@Override
	public List<AppCategory> getAppCategoryListByParentId(Integer parentId)
			 {
		// TODO Auto-generated method stub
		return mapper.getAppCategoryListByParentId(parentId);
	}

}
