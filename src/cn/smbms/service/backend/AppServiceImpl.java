package cn.smbms.service.backend;

import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

import cn.smbms.dao.appinfo.AppInfoMapper;
import cn.smbms.pojo.AppInfo;

@Service
public class AppServiceImpl implements AppService {
	@Resource
	private AppInfoMapper mapper;
	
	@Override
	public AppInfo getAppInfo(Integer id)  {
		// TODO Auto-generated method stub
		return mapper.getAppInfo(id, null);
	}

	@Override
	public List<AppInfo> getAppInfoList(String querySoftwareName,
									Integer queryCategoryLevel1,
									Integer queryCategoryLevel2, Integer queryCategoryLevel3,
									Integer queryFlatformId,Integer currentPageNo,
									Integer pageSize) {
		// TODO Auto-generated method stub
		return mapper.getAppInfoList(querySoftwareName, 1, queryCategoryLevel1, queryCategoryLevel2, 
				                 queryCategoryLevel3, queryFlatformId, null, (currentPageNo-1)*pageSize, pageSize);
	}

	@Override
	public int getAppInfoCount(String querySoftwareName,
							Integer queryCategoryLevel1, 
							Integer queryCategoryLevel2,
							Integer queryCategoryLevel3, 
							Integer queryFlatformId)
							{
		// TODO Auto-generated method stub
		return mapper.getAppInfoCount(querySoftwareName, 1, queryCategoryLevel1, queryCategoryLevel2, 
									queryCategoryLevel3, queryFlatformId, null);
	}

	@Override
	public boolean updateSatus(Integer status, Integer id) {
		// TODO Auto-generated method stub
		boolean flag = false;
		if(mapper.updateSatus(status, id) > 0 ){
			flag = true;
		}
		return flag;
	}
}
