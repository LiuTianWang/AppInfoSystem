package cn.smbms.service.developer;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.smbms.dao.datadictionary.DataDictionaryMapper;
import cn.smbms.pojo.DataDictionary;

@Service
public class DataDictionaryServiceImpl implements DataDictionaryService {
	
	@Resource
	private DataDictionaryMapper mapper;
	
	@Override
	public List<DataDictionary> getDataDictionaryList(String typeCode)
			 {
		// TODO Auto-generated method stub
		return mapper.getDataDictionaryList(typeCode);
	}

}
