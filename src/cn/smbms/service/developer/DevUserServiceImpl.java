package cn.smbms.service.developer;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

import cn.smbms.dao.devuser.DevUserMapper;
import cn.smbms.pojo.DevUser;

@Service
public class DevUserServiceImpl implements DevUserService {
	@Resource
	private DevUserMapper mapper;
	@Override
	public DevUser login(String devCode, String devPassword) {
		// TODO Auto-generated method stub
		DevUser user = null;
		try {
			user = mapper.getLoginUser(devCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//匹配密码
		if(null != user){
			if(!user.getDevPassword().equals(devPassword))
				user = null;
		}
		return user;
	}

}
