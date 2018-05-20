package cn.smbms.service.backend;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;

import cn.smbms.dao.backenduser.BackendUserMapper;
import cn.smbms.pojo.BackendUser;

@Service
public class BackendUserServiceImpl implements BackendUserService {
	@Resource
	private BackendUserMapper mapper;
	
	@Override
	public BackendUser login(String userCode, String userPassword){
		// TODO Auto-generated method stub
		BackendUser user = null;
		user = mapper.getLoginUser(userCode);
		//匹配密码
		if(null != user){
			if(!user.getUserPassword().equals(userPassword))
				user = null;
		}
		return user;
	}

}
