package cn.smbms.controller.developer;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.mysql.jdbc.StringUtils;
import com.sun.istack.internal.logging.Logger;
import cn.smbms.pojo.AppCategory;
import cn.smbms.pojo.AppInfo;
import cn.smbms.pojo.AppVersion;
import cn.smbms.pojo.DataDictionary;
import cn.smbms.pojo.DevUser;
import cn.smbms.service.developer.AppCategoryService;
import cn.smbms.service.developer.AppInfoService;
import cn.smbms.service.developer.AppVersionService;
import cn.smbms.service.developer.DataDictionaryService;
import cn.smbms.service.developer.DevUserService;
import cn.smbms.tools.Constants;
import cn.smbms.tools.PageSupport;
@Controller
@RequestMapping("dev")
public class devController {
	Logger logger  = Logger.getLogger(devController.class); 
	@Resource
	private DevUserService devUserService;
	@Resource
	private AppInfoService appInfoService;
	@Resource 
	private DataDictionaryService dataDictionaryService;
	@Resource 
	private AppCategoryService appCategoryService;
	@Resource
	private AppVersionService appVersionService;
	/**
	 * 进入登录页面
	 * @return
	 */
	@RequestMapping("loginDev")
	public String log() {

		return "devlogin";
	}
	/**
	 * 进行登录验证
	 * @param request
	 * @return
	 */
	@RequestMapping("dologin")
	public String login(HttpServletRequest request) {
		String devCode = request.getParameter("devCode");
		String devPassword = request.getParameter("devPassword");
		DevUser dUser= devUserService.login(devCode, devPassword);
		request.getSession().setAttribute(Constants.DEV_USER_SESSION,dUser);
		if (dUser!=null) {
			return "developer/main";
		}
		return "403";
	}
	/**
	 * 显示三级菜单及所有信息查询操作
	 * @param model
	 * @param session
	 * @param querySoftwareName
	 * @param _queryStatus
	 * @param _queryCategoryLevel1
	 * @param _queryCategoryLevel2
	 * @param _queryCategoryLevel3
	 * @param _queryFlatformId
	 * @param pageIndex
	 * @return
	 */
	@RequestMapping("list")
	public String devUserList(Model model,HttpSession session,
			@RequestParam(value="querySoftwareName",required=false) String querySoftwareName,
			@RequestParam(value="queryStatus",required=false) String _queryStatus,
			@RequestParam(value="queryCategoryLevel1",required=false) String _queryCategoryLevel1,
			@RequestParam(value="queryCategoryLevel2",required=false) String _queryCategoryLevel2,
			@RequestParam(value="queryCategoryLevel3",required=false) String _queryCategoryLevel3,
			@RequestParam(value="queryFlatformId",required=false) String _queryFlatformId,
			@RequestParam(value="pageIndex",required=false) String pageIndex) {
		logger.info("getAppInfoList -- > querySoftwareName: " + querySoftwareName);
		logger.info("getAppInfoList -- > queryStatus: " + _queryStatus);
		logger.info("getAppInfoList -- > queryCategoryLevel1: " + _queryCategoryLevel1);
		logger.info("getAppInfoList -- > queryCategoryLevel2: " + _queryCategoryLevel2);
		logger.info("getAppInfoList -- > queryCategoryLevel3: " + _queryCategoryLevel3);
		logger.info("getAppInfoList -- > queryFlatformId: " + _queryFlatformId);
		logger.info("getAppInfoList -- > pageIndex: " + pageIndex);

		Integer devId = ((DevUser)session.getAttribute(Constants.DEV_USER_SESSION)).getId();
		List<AppInfo> appInfoList = null;
		List<DataDictionary> statusList = null;
		List<DataDictionary> flatFormList = null;
		List<AppCategory> categoryLevel1List = null;//列出一级分类列表，注：二级和三级分类列表通过异步ajax获取
		List<AppCategory> categoryLevel2List = null;
		List<AppCategory> categoryLevel3List = null;
		//页面容量
		int pageSize = Constants.pageSize;
		//当前页码
		Integer currentPageNo = 1;

		if(pageIndex != null){
			try{
				currentPageNo = Integer.valueOf(pageIndex);
			}catch (NumberFormatException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		Integer queryStatus = null;
		if(_queryStatus != null && !_queryStatus.equals("")){
			queryStatus = Integer.parseInt(_queryStatus);
		}
		Integer queryCategoryLevel1 = null;
		if(_queryCategoryLevel1 != null && !_queryCategoryLevel1.equals("")){
			queryCategoryLevel1 = Integer.parseInt(_queryCategoryLevel1);
		}
		Integer queryCategoryLevel2 = null;
		if(_queryCategoryLevel2 != null && !_queryCategoryLevel2.equals("")){
			queryCategoryLevel2 = Integer.parseInt(_queryCategoryLevel2);
		}
		Integer queryCategoryLevel3 = null;
		if(_queryCategoryLevel3 != null && !_queryCategoryLevel3.equals("")){
			queryCategoryLevel3 = Integer.parseInt(_queryCategoryLevel3);
		}
		Integer queryFlatformId = null;
		if(_queryFlatformId != null && !_queryFlatformId.equals("")){
			queryFlatformId = Integer.parseInt(_queryFlatformId);
		}

		//总数量（表）
		int totalCount = 0;
		try {
			totalCount = appInfoService.getAppInfoCount(querySoftwareName, queryStatus, queryCategoryLevel1, queryCategoryLevel2, queryCategoryLevel3, queryFlatformId, devId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//总页数
		PageSupport pages = new PageSupport();
		pages.setCurrentPageNo(currentPageNo);
		pages.setPageSize(pageSize);
		pages.setTotalCount(totalCount);
		int totalPageCount = pages.getTotalPageCount();
		//控制首页和尾页
		if(currentPageNo < 1){
			currentPageNo = 1;
		}else if(currentPageNo > totalPageCount){
			currentPageNo = totalPageCount;
		}
		try {
			appInfoList = appInfoService.getAppInfoList(querySoftwareName, queryStatus, queryCategoryLevel1, queryCategoryLevel2, queryCategoryLevel3, queryFlatformId, devId, currentPageNo, pageSize);
			statusList = this.getDataDicList("APP_STATUS");
			flatFormList = this.getDataDicList("APP_FLATFORM");
			categoryLevel1List = appCategoryService.getAppCategoryListByParentId(null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.addAttribute("appInfoList", appInfoList);
		model.addAttribute("statusList", statusList);
		model.addAttribute("flatFormList", flatFormList);
		model.addAttribute("categoryLevel1List", categoryLevel1List);
		model.addAttribute("pages", pages);
		model.addAttribute("queryStatus", queryStatus);
		model.addAttribute("querySoftwareName", querySoftwareName);
		model.addAttribute("queryCategoryLevel1", queryCategoryLevel1);
		model.addAttribute("queryCategoryLevel2", queryCategoryLevel2);
		model.addAttribute("queryCategoryLevel3", queryCategoryLevel3);
		model.addAttribute("queryFlatformId", queryFlatformId);

		//二级分类列表和三级分类列表---回显
		if(queryCategoryLevel2 != null && !queryCategoryLevel2.equals("")){
			categoryLevel2List = getCategoryList(queryCategoryLevel1.toString());
			model.addAttribute("categoryLevel2List", categoryLevel2List);
		}
		if(queryCategoryLevel3 != null && !queryCategoryLevel3.equals("")){
			categoryLevel3List = getCategoryList(queryCategoryLevel2.toString());
			model.addAttribute("categoryLevel3List", categoryLevel3List);
		}
		return "developer/appinfolist";
	}
	public List<DataDictionary> getDataDictionaryList(String typeCode){
		List<DataDictionary> dataDictionaryList = null;
		try {
			dataDictionaryList = dataDictionaryService.getDataDictionaryList(typeCode);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataDictionaryList;
	}

	/**
	 * 根据typeCode查询出相应的数据字典列表
	 * @param pid
	 * @return
	 */
	@RequestMapping(value="/datadictionarylist.json",method=RequestMethod.GET)
	@ResponseBody
	public List<DataDictionary> getDataDicList (@RequestParam String tcode){
		return this.getDataDictionaryList(tcode);
	}
	/**
	 * 加载平台
	 * @param tcode
	 * @return
	 */
	@RequestMapping(value="/datadictionarylist2.json",method=RequestMethod.GET,produces = "application/json;charset=utf-8")
	@ResponseBody
	public Object getDataDicList2 (@RequestParam String tcode){
		//List<DataDictionary> 
		return JSONArray.toJSONString(this.getDataDictionaryList(tcode));
	}
	/**
	 * 根据parentId查询出相应的分类级别列表
	 * @param pid
	 * @return
	 */
	@RequestMapping(value="/categorylevellist.json",method=RequestMethod.GET,produces = "application/json;charset=utf-8")
	@ResponseBody
	public Object getAppCategoryList (@RequestParam String pid){
		if(pid.equals("")) pid = null;
		return JSONArray.toJSONString(getCategoryList(pid));
	}
	/**
	 * 查询分类列表
	 * @param pid
	 * @return
	 */
	public List<AppCategory> getCategoryList (String pid){
		List<AppCategory> categoryLevelList = null;
		try {
			categoryLevelList = appCategoryService.getAppCategoryListByParentId(pid==null?null:Integer.parseInt(pid));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return categoryLevelList;
	}
	/**
	 * 根据id 进入查看页面显示信息
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/appview/{id}",method=RequestMethod.GET)
	public String view(@PathVariable String id,Model model){
		AppInfo appInfo =  appInfoService.getAppInfo(Integer.parseInt(id),null);
		List<AppVersion> appVersionList = appVersionService.getAppVersionList(Integer.parseInt(id));
		model.addAttribute("appVersionList", appVersionList);
		model.addAttribute(appInfo);
		return "developer/appinfoview";
		
	}
	@RequestMapping(value="/delapp.json")
	@ResponseBody
	public Object delApp(@RequestParam String id){
		HashMap<String, String> resultMap = new HashMap<String, String>();
		if(StringUtils.isNullOrEmpty(id)){
			resultMap.put("delResult", "notexist");
		}else{
			try {
				if(appInfoService.appsysdeleteAppById(Integer.parseInt(id)))
					resultMap.put("delResult", "true");
				else
					resultMap.put("delResult", "false");
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return JSONArray.toJSONString(resultMap);
	}
	/**
	 * 进入添加页面
	 */
	@RequestMapping("/addJSP")
	public Object addJSP() {
		return "developer/appinfoadd";
	}
	/**
	 * 保存app信息
	 * @return
	 */
	@RequestMapping("/appinfoaddsave")
	public String addinfo(AppInfo info,@RequestParam(value="a_logoPicPath",required=false) MultipartFile attach,HttpSession session,HttpServletRequest request) {
		String logoPicPath =  null;
		String logoLocPath =  null;
		if(!attach.isEmpty()){
			String path = request.getSession().getServletContext().getRealPath("statics"+java.io.File.separator+"uploadfiles");
			logger.info("uploadFile path: " + path);
			String oldFileName = attach.getOriginalFilename();//原文件名
			String prefix = FilenameUtils.getExtension(oldFileName);//原文件后缀
			int filesize = 500000;
			if(attach.getSize() > filesize){//上传大小不得超过 50k
				request.setAttribute("fileUploadError", Constants.FILEUPLOAD_ERROR_4);
				return "developer/appinfoadd";
            }else if(prefix.equalsIgnoreCase("jpg") || prefix.equalsIgnoreCase("png") 
			   ||prefix.equalsIgnoreCase("jepg") || prefix.equalsIgnoreCase("pneg")){//上传图片格式
				 String fileName = info.getAPKName() + ".jpg";//上传LOGO图片命名:apk名称.apk
				 File targetFile = new File(path,fileName);
				 if(!targetFile.exists()){
					 targetFile.mkdirs();
				 }
				 try {
					attach.transferTo(targetFile);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					request.setAttribute("fileUploadError", Constants.FILEUPLOAD_ERROR_2);
					return "developer/appinfoadd";
				} 
				 logoPicPath = request.getContextPath()+"/statics/uploadfiles/"+fileName;
				 logoLocPath = path+File.separator+fileName;
			}else{
				request.setAttribute("fileUploadError", Constants.FILEUPLOAD_ERROR_3);
				return "developer/appinfoadd";
			}
		}
		info.setCreatedBy(((DevUser)session.getAttribute(Constants.DEV_USER_SESSION)).getId());
		info.setCreationDate(new Date());
		info.setLogoPicPath(logoPicPath);
		info.setLogoLocPath(logoLocPath);
		info.setDevId(((DevUser)session.getAttribute(Constants.DEV_USER_SESSION)).getId());
		info.setStatus(1);
		if (appInfoService.add(info)) {
			return "redirect:/dev/list";
		}
		return "redirect:/dev/addJSP";
	}
	/**
	 * 修改app基础信息
	 */
	@RequestMapping("appinfomodify")
	public String UpdateJSP(@RequestParam int id,Model model) {
		AppInfo appInfo= appInfoService.getAppInfo(id, null);
		model.addAttribute(appInfo);
		return "developer/appinfomodify";
	}
	/**
	 * 修改保存信息
	 */
	@RequestMapping("appinfomodifysave")
	public String UpdateSave(AppInfo appInfo) {
		
		boolean flag= appInfoService.modify(appInfo);
		if (flag) {
			return "redirect:/dev/list";
		}
		return "redirect:/dev/appinfomodify";
	}
	/**
	 * 显示最新版本信息，进入新增版本信息页面
	 */
	@RequestMapping("appversionadd")
	public String addApp(@RequestParam int id,Model model) {
		List<AppVersion> appVersion= appVersionService.getAppVersionList(id);
		AppVersion appVersion1 = new AppVersion();
		appVersion1.setAppId(id);
		model.addAttribute("appVersionList",appVersion);
		model.addAttribute("appVersion", appVersion1);
		return "developer/appversionadd";
	}
	/**
	 * 新增版本信息
	 */
	@RequestMapping(value="/addversionsave",method=RequestMethod.POST)
	public String addVersio(AppVersion appVersion,@RequestParam(value="a_downloadLink",required=false) MultipartFile attach,HttpSession session,HttpServletRequest request) {
		String downloadLink =  null;
		String apkLocPath = null;
		String apkFileName = null;
		if(!attach.isEmpty()){
			String path = request.getSession().getServletContext().getRealPath("statics"+File.separator+"uploadfiles");
			logger.info("uploadFile path: " + path);
			String oldFileName = attach.getOriginalFilename();//原文件名
			String prefix = FilenameUtils.getExtension(oldFileName);//原文件后缀
			if(prefix.equalsIgnoreCase("apk")){//apk文件命名：apk名称+版本号+.apk
				 String apkName = null;
				 AppInfo wqe= null;
				 try {
					 System.out.println(appVersion.getAppId());
					 wqe = appInfoService.getAppInfo(appVersion.getAppId(),null);
					 apkName = wqe.getAPKName();
				 } catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					
				 }
				 if(apkName == null || "".equals(apkName)){
					 return "redirect:/dev/appversionadd?id="+appVersion.getAppId()
							 +"&error=error1";
				 }
				 apkFileName = apkName + "-" +appVersion.getVersionNo() + ".apk";
				 File targetFile = new File(path,apkFileName);
				 if(!targetFile.exists()){
					 targetFile.mkdirs();
				 }
				 try {
					attach.transferTo(targetFile);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return "redirect:/dev/appversionadd?id="+appVersion.getAppId()
							 +"&error=error2";
				} 
				downloadLink = request.getContextPath()+"/statics/uploadfiles/"+apkFileName;
				apkLocPath = path+File.separator+apkFileName;
			}else{
				return "redirect:/dev/appversionadd?id="+appVersion.getAppId()
						 +"&error=error3";
			}
		}
		appVersion.setCreatedBy(((DevUser)session.getAttribute(Constants.DEV_USER_SESSION)).getId());
		appVersion.setCreationDate(new Date());
		appVersion.setDownloadLink(downloadLink);
		appVersion.setApkLocPath(apkLocPath);
		appVersion.setApkFileName(apkFileName);
		try {
			if(appVersionService.appsysadd(appVersion)){
				return "redirect:/dev/list";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "redirect:/dev/appversionadd?id="+appVersion.getAppId();
	}
	/**
	 * 修改最新版本信息 显示页面
	 * @param vid
	 * @param aid
	 * @param model
	 * @return
	 */
	@RequestMapping("appversionmodify")
	public String appverUpdate(@RequestParam int vid,@RequestParam int aid,Model model) {
		List<AppVersion> appVersionList=appVersionService.getAppVersionList(aid);
		AppVersion appVersion= appVersionService.getAppVersionById(vid);
		model.addAttribute(appVersionList);
		model.addAttribute(appVersion);
		return "developer/appversionmodify";
	}
	/**
	 * 修改最新版本信息方法
	 * @param appVersion
	 * @return
	 */
	@RequestMapping("appversionmodifysave")
	public String updateSeva(AppVersion appVersion) {
		boolean flag =appVersionService.modify(appVersion);
		if (flag) {
			return "redirect:/dev/list";
		}
		return "redirect:/dev/appversionmodifysave";
	}
	@RequestMapping(value="/{appid}/sale",method=RequestMethod.PUT)
	@ResponseBody
	public Object sale(@PathVariable String appid,HttpSession session){
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		Integer appIdInteger = 0;
		try{
			appIdInteger = Integer.parseInt(appid);
		}catch(Exception e){
			appIdInteger = 0;
		}
		resultMap.put("errorCode", "0");
		resultMap.put("appId", appid);
		if(appIdInteger>0){
			try {
				DevUser devUser = (DevUser)session.getAttribute(Constants.DEV_USER_SESSION);
				AppInfo appInfo = new AppInfo();
				appInfo.setId(appIdInteger);
				appInfo.setModifyBy(devUser.getId());
				if(appInfoService.appsysUpdateSaleStatusByAppId(appInfo)){
					resultMap.put("resultMsg", "success");
				}else{
					resultMap.put("resultMsg", "success");
				}		
			} catch (Exception e) {
				resultMap.put("errorCode", "exception000001");
			}
		}else{
			//errorCode:0为正常
			resultMap.put("errorCode", "param000001");
		}
		
		/*
		 * resultMsg:success/failed
		 * errorCode:exception000001
		 * appId:appId
		 * errorCode:param000001
		 */
		return resultMap;
	}
	@RequestMapping(value="/logout")
	public String logout(HttpSession session){
		//清除session
		session.removeAttribute(Constants.DEV_USER_SESSION);
		return "devlogin";
	}
}
