package com.ets.dictionary.userpro.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ets.common.UUIDUtils;
import com.ets.dictionary.userpro.dao.UserProDao;
import com.ets.dictionary.userpro.entity.nb_userpro_dict;

/**
 * 用户性质字典表操作数据库类
 * @author Administrator
 *
 */
@Service
@Transactional
public class UserProService {
	
	@Resource
	UserProDao userProDao;

	/**
	 * 根据条件查询用户性质字典表
	 * @param map
	 * @return
	 */
	public List<nb_userpro_dict> getUserPro(Map<String, Object> map) {
		try {
			return userProDao.selectUserPro(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	

	/**
	 * 根据条件查询用户性质字典表总数
	 * @return
	 */
	public long getCount() {
		try {
			return userProDao.selectCount();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 操作用户性质字段数据
	 * @param userpro
	 */
	public void opentionUserPro(nb_userpro_dict userpro) {
		try {
			if(userpro.getId()!=null && !userpro.getId().equals(""))
			{
				userProDao.updateUserPro(userpro);
			}
			else
			{
				userpro.setId(UUIDUtils.getUUID());
				userpro.setCtime(new Timestamp(System.currentTimeMillis()));
				userpro.setRemarks(userpro.getDescribe());
				userProDao.insertUserPro(userpro);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据ID查询用户性质数据字典信息
	 * @param id
	 * @return
	 */
	public nb_userpro_dict infoUserPro(String id) {
		try {
			return userProDao.infoUserPro(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * 根据ID删除用户性质数据字典信息
	 * @param id
	 */
	public void deleteUserPro(String[] id) {
		try {
			userProDao.deleteUserPro(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public String infoUserPro(String[] id) {
		try {
			String usertype = "";
			List<nb_userpro_dict> u = userProDao.infoUserProList(id);
			if(u.size() > 0){
				for (nb_userpro_dict user : u) {
					usertype += user.getUsertype()+",";
				}
			}
			return usertype.substring(0,usertype.length()-1);
					
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	public List<nb_userpro_dict> getUserProAll() {
		try {
			return userProDao.selectUserProAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
