package cn.smbms.service.developer;

import java.util.List;

import cn.smbms.pojo.AppVersion;

public interface AppVersionService {
	/**
	 * 根据appId查询相应的app版本列表
	 * @param appId
	 * @return
	 * @throws Exception
	 */
	public List<AppVersion> getAppVersionList(Integer appId);
	/**
	 * 新增app版本信息，并更新app_info表的versionId字段
	 * @param appVersion
	 * @return
	 * @throws Exception
	 */
	public boolean appsysadd(AppVersion appVersion);
	/**
	 * 根据id获取AppVersion
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public AppVersion getAppVersionById(Integer id);
	
	/**
	 * 修改app版本信息
	 * @param appVersion
	 * @return
	 * @throws Exception
	 */
	public boolean modify(AppVersion appVersion);
	
	/**
	 * 删除apk文件
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public boolean deleteApkFile(Integer id);
}
