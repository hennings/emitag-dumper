package net.spjelkavik.emit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

public class EtimingReader {

		static Logger log = Logger.getLogger(EtimingReader.class.getName());
		
		JdbcTemplate jdbcTemplate;

		public JdbcTemplate getJdbcTemplate() {
			return jdbcTemplate;
		}

		public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
			this.jdbcTemplate = jdbcTemplate;
		}

		public boolean updateResults(int startNumber, Frame frame) {
			String time = frame.getRunningTime();
			
/*
			log.debug("update new set ecard="+frame+" where startno="+startNumber);
			int r2 = jdbcTemplate.update("update name set ecard=? where startno=?", 
					new Object[] {frame,startNumber} );
			log.debug("database updated " + r0+", " + r1+", "+r2);
			
			jdbcTemplate.execute("select * from Name where ecard=" + frame);
*/
			return true;
		}

		public boolean updateEcardAnonymous(int startNumber, int ecard) {
			int newEcard = startNumber;
			if (newEcard<200000) newEcard=startNumber + 200000;
			
			log.debug("rename old");
			int r0 = jdbcTemplate.update("update name set ecard=? where ecard=?", 
					new Object[] {newEcard, ecard} );
			int r1 = jdbcTemplate.update("update ecard set ecardno=? where ecardno=?", 
					new Object[] {newEcard, ecard} );
			
			log.debug("update new set ecard="+ecard+" where startno="+startNumber);
			int r2 = jdbcTemplate.update("update name set ecard=? where startno=?", 
					new Object[] {ecard,startNumber} );
			log.debug("database updated " + r0+", " + r1+", "+r2);
			
			jdbcTemplate.execute("select * from Name where ecard=" + ecard);

			if (r2>0) return true;
			return false;
		}

		@SuppressWarnings("unchecked")
		public Map<String,String> getRunner(int startNumber) {
			RowMapper rse = new RowMapper() {

				public Object mapRow(ResultSet rs, int rowNum) throws SQLException, DataAccessException {
					final Map<String, String> result = new HashMap<String, String>();
					result.put("id", rs.getString("id"));
					result.put("name", rs.getString("name"));
					result.put("ename", rs.getString("ename"));
					result.put("ecard", rs.getString("ecard"));
					result.put("startno", rs.getString("startno"));
					return result;
				}
				
			};

	
			 List<Map<String,String>> r = jdbcTemplate.query(
					"select n.id,n.ename, n.name,n.times, n.place, n.class, n.cource, n.starttime, "+
					"n.status, n.statusmsg, n.startno, "+
					"n.intime, n.ecard, n.changed, n.team, t.name as team_name from Name n, Team t "+
					"where n.team=t.code and n.startno=? "
					,new Object[] { new Integer(startNumber)}, rse);
					
			log.info("template returned: " + r);
			if (r.size()>0) return r.get(0);
			return null;
		}

}