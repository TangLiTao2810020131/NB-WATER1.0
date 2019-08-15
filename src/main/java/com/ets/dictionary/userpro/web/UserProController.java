package com.ets.dictionary.userpro.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ets.system.log.opr.entity.tb_log_opr;
import com.ets.system.log.opr.service.LogOprService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ets.common.PageListData;
import com.ets.dictionary.userpro.entity.nb_userpro_dict;
import com.ets.dictionary.userpro.service.UserProService;
import com.ets.utils.Message;
import com.google.gson.Gson;

/**
 * 用户性质数据字典
 * @author wh
 *
 */
@Controller
@RequestMapping("userpro")
public class UserProController {

	@Resource
	UserProService userProService;
	@Resource
	LogOprService logService;

	String baseUrl = "dictionary/userpro/";
	
	String modulename = "数据字典-用户性质";

	@RequestMapping("list")
	public String list(HttpServletRequest request)
	{
		tb_log_opr log = new tb_log_opr();
		log.setModulename(modulename);
		log.setOprcontent("访问用户性质列表");
		logService.addLog(log);
		return baseUrl+"userpro-list";
	}

	@RequestMapping("input")
	public String input(HttpServletRequest request,String id)
	{
		nb_userpro_dict userpro = null;
		if(id!=null && !id.equals(""))
		{
			userpro = userProService.infoUserPro(id);
		}
		request.setAttribute("userpro", userpro);
		return baseUrl+"userpro-input";
	}
	
	@RequestMapping("info")
	public String info(HttpServletRequest request,String id)
	{
		nb_userpro_dict userpro = null;
		if(id!=null && !id.equals(""))
		{
			userpro = userProService.infoUserPro(id);
			tb_log_opr log = new tb_log_opr();
			log.setModulename(modulename);
			log.setOprcontent("查看,"+modulename+"["+userpro.getUsertype()+"]");
			logService.addLog(log);
		}
		request.setAttribute("userpro", userpro);
		return baseUrl+"userpro-info";
	}

	/**
	 * 分页查询用户性质数据字典集合
	 * @param page
	 * @param limit
	 * @return
	 */
	@RequestMapping(value="listData" ,produces = "application/json; charset=utf-8")
	@ResponseBody
	public String listData(int page,int limit)
	{
		Gson gson = new Gson();
		try {
			//System.out.println("page="+page+",limit="+limit);
			Map<String,Object> map = new HashMap<String,Object>();
			//			map.put("page", (page-1)*limit);//mysql
			//			map.put("limit", limit);//mysql
			map.put("page", (page)*limit);//oracle
			map.put("limit", (page-1)*limit);//oracle

			List<nb_userpro_dict> userpro = userProService.getUserPro(map);
			long count = userProService.getCount();


			PageListData<nb_userpro_dict> pageData = new PageListData<nb_userpro_dict>();

			pageData.setCode("0");
			pageData.setCount(count);
			pageData.setMessage("");
			pageData.setData(userpro);


			String listJson = gson.toJson(pageData);
			return listJson;
		} catch (Exception e) {
			e.printStackTrace();
			return gson.toJson(new Message("2","操作失败!"));
		}
	}

	@RequestMapping(value="save" ,produces = "application/json; charset=utf-8")
	@ResponseBody
	public String save(nb_userpro_dict userpro)
	{
		Gson gson = new Gson();
		try {
			tb_log_opr log = new tb_log_opr();
			log.setModulename(modulename);
			if(userpro.getId() == null || userpro.getId().equals(""))
			{
				log.setOprcontent("新增,"+modulename+"["+userpro.getUsertype()+"]");
			}
			else
			{
				log.setOprcontent("修改,"+modulename+"["+userpro.getUsertype()+"]");
			}
			logService.addLog(log);
			userProService.opentionUserPro(userpro);
			return gson.toJson(new Message("1","操作成功!"));
		} catch (Exception e) {
			e.printStackTrace();
			return gson.toJson(new Message("2","操作失败!"));
		}
	}
	
	@RequestMapping(value="delete" )
	@ResponseBody
	public String delete(String id[])
	{
		Gson gson = new Gson();
		try {
			String usertype = userProService.infoUserPro(id);
			userProService.deleteUserPro(id);
			tb_log_opr log = new tb_log_opr();
			log.setModulename(modulename);
			log.setOprcontent("删除,"+modulename+"["+usertype+"]");
			logService.addLog(log);
			return gson.toJson(new Message("1","删除成功!"));
		} catch (Exception e) {
			e.printStackTrace();
			return gson.toJson(new Message("2","删除失败!"));
		}
	}
}
