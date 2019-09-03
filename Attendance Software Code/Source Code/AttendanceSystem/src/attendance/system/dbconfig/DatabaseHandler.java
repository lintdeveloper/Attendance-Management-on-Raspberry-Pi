/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attendance.system.dbconfig;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

/**
 *
 * @author Abbas Yunusa Ogaji
 * 
 * Documentation on 25th May 2018: Tables created : Students profile, Lecturer profile, lecturer Courses, Attendance Report, lecture Attendance 
 */
public class DatabaseHandler {
 
	private static DatabaseHandler handler = null;
	private static Statement stmt = null;
	private static Connection conn = null;


	private DatabaseHandler(){
		createConnection();
                
	}

	public static DatabaseHandler getInstance(){
		if(handler == null){
			handler = new DatabaseHandler();
		}
		return handler;
	}
        public static void createTables(){
                createStudentsTable();
                createStudentCourseRegTable();
                createLecturerTable();
                createLecturerCoursesTable();
                createLectureAttendanceTable();
                createExamAttendanceTable();
                createAttendanceReportTable();
        }
	private void createConnection(){

		try {

			Class.forName(DbSqliteConstants.SQLITE_JDBC_DRIVER).newInstance();
			conn = DriverManager.getConnection(DbSqliteConstants.SQLITE_DB_CONNECTION_STRING);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
        
        public static Connection getConnection(){
            if(handler == null){
                handler = new DatabaseHandler();
            }
            return conn;
        }
        
        /**
         * FUNCTIONS FOR DATABASE TABLE CREATION
         * @param
         * @return
         */
	private static void createLecturerTable() {
		try{
			String TABLE_NAME = "LECTURER";
			stmt = conn.createStatement();
			DatabaseMetaData dbm = conn.getMetaData();
			ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(),null);
			if(tables.next()){
				System.out.println(TABLE_NAME+" already exists");
			}else{
				stmt.execute(DbSqliteConstants.SQLITE_CREATE_LECTURER_TABLE_SQL);
			}
			}catch(SQLException e){
				System.err.println(e.getLocalizedMessage());
			}

	}
        private static void createLecturerCoursesTable() {
		try{
			String TABLE_NAME = "LECTURER_COURSES";
			stmt = conn.createStatement();
			DatabaseMetaData dbm = conn.getMetaData();
			ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(),null);
			if(tables.next()){
				System.out.println(TABLE_NAME+" already exists");
			}else{
				stmt.execute(DbSqliteConstants.SQLITE_CREATE_LECTURER_COURSES_TABLE_SQL);
			}
			}catch(SQLException e){
				System.err.println(e.getLocalizedMessage());
			}

	}
        private static void createStudentsTable() {
		try{
			String TABLE_NAME = "students";
			stmt = conn.createStatement();
			DatabaseMetaData dbm = conn.getMetaData();
			ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(),null);
			if(tables.next()){
				System.out.println(TABLE_NAME+" already exists");
			}else{
				stmt.execute(DbSqliteConstants.SQLITE_CREATE_STUDENTS_TABLE_SQL);
			}
			}catch(SQLException e){
				System.err.println(e.getLocalizedMessage());
			}

	}
        private static void createStudentCourseRegTable(){
        		try{
			String TABLE_NAME = DbSqliteConstants.STUDENT_REG_TABLE_NAME;
			stmt = conn.createStatement();
			DatabaseMetaData dbm = conn.getMetaData();
			ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(),null);
			if(tables.next()){
				System.out.println(TABLE_NAME+" already exists");
			}else{
				stmt.execute(DbSqliteConstants.SQLITE_CREATE_STUDENT_COURSEREG_TABLE_SQL);
			}
			}catch(SQLException e){
				System.err.println(e.getMessage());
			}

        }
        private static void createLectureAttendanceTable() {
		try{
			String TABLE_NAME = "LECTURE_ATTENDANCE";
			stmt = conn.createStatement();
			DatabaseMetaData dbm = conn.getMetaData();
			ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(),null);
			if(tables.next()){
//				System.out.println(TABLE_NAME+" already exists");
			}else{
				stmt.execute(DbSqliteConstants.SQLITE_CREATE_LECTURE_ATTENDANCE_TABLE_SQL);
			}
			}catch(SQLException e){
				System.err.println(e.getMessage());
			}

	}
        private static void createExamAttendanceTable() {
		try{
			String TABLE_NAME = "EXAM_ATTENDANCE";
			stmt = conn.createStatement();
			DatabaseMetaData dbm = conn.getMetaData();
			ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(),null);
			if(tables.next()){
				System.out.println(TABLE_NAME+" already exists");
			}else{
				stmt.execute(DbSqliteConstants.SQLITE_CREATE_EXAM_ATTENDANCE_TABLE_SQL);
			}
			}catch(SQLException e){
				System.err.println(e.getLocalizedMessage());
			}

	}       
        private static void createAttendanceReportTable() {
		try{
			String TABLE_NAME = "ATTENDANCE_REPORT_STATUS";
			stmt = conn.createStatement();
			DatabaseMetaData dbm = conn.getMetaData();
			ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(),null);
			if(tables.next()){
				System.out.println(TABLE_NAME+" already exists");
			}else{
				stmt.execute(DbSqliteConstants.SQLITE_ATTENDANCE_REPORT_STATUS_TABLE_SQL);
			}
			}catch(SQLException e){
				System.err.println(e.getLocalizedMessage());
			}

	}
        /**
         * FUNCTION FOR QUERYING DATABASE TABLES
         * @param table
         * @return 
         */
        public static boolean dataExistIn(String table){
           boolean cond = false;
            try{
                    Statement st = conn.createStatement();
                    ResultSet data = st.executeQuery("SELECT * FROM "+table+"");
                    cond = data.next();
                    return cond;
            }catch(SQLException e){
                   return false; 
            }
        }       
	public ResultSet exeQuery(String sql){
		ResultSet result;
		try{
			stmt = conn.createStatement();
			result = stmt.executeQuery(sql);
		}catch(SQLException e){
			System.out.println("Error SQLException at exeQuery() method :" + e.getLocalizedMessage());
			return null;
		}
		return result;
	}
	public boolean exeAction(String sql){
		try{
			stmt = conn.createStatement();
			stmt.execute(sql);
			return true;
		}catch(SQLException e){
			JOptionPane.showMessageDialog(null, "Error:" + e.getMessage(), "Error occured", JOptionPane.ERROR_MESSAGE);
			System.out.println("Error SQLException at exeAction() method :" + e.getLocalizedMessage());
			return false;
		}
	}
        //THE METHOD WAS CREATED SPECIFICALLY FOR THE ATTENDANCE RECORDING PROCESS
        //IT IS SURROUNDED MY A TRY-CATCH BLOCK THAT DOESN'T RETURN ANY ERROR OR WARNING MESSAGE
        public boolean exeAttendanceQuery(String sql){
		try{
			stmt = conn.createStatement();
			stmt.execute(sql);
			return true;
		}catch(SQLException e){
			return false;
		}
	}

}
