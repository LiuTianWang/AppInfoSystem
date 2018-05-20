package cn.smbms.dao.devuser;

import org.apache.ibatis.annotations.Param;

import cn.smbms.pojo.DevUser;

public interface DevUserMapper {
	/**
	 * 通过userCode获取User
	 * @param userCode
	 * @return
	 * @throws Exception
	 */
	public DevUser getLoginUser(@Param("devCode")String devCode);
}
