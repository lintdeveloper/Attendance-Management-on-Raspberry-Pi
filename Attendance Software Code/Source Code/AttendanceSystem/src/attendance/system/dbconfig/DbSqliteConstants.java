package attendance.system.dbconfig;
/**
 *
 * @author Abbasogaji
 */
public class DbSqliteConstants {
        public static final String SQLITE_DB_CONNECTION_STRING = "jdbc:sqlite:attendance.db";
        public static final String SQLITE_JDBC_DRIVER = "org.sqlite.JDBC";
        public static final String STUDENTS_TABLE_NAME = "students",
                                   STUDENT_REG_TABLE_NAME = "STUDENT_COURSE_REG",
                                   LECTURER_TABLE_NAME = "LECTURER",
                                   LECTURER_COURSES_TABLE_NAME = "LECTURER_COURSES",
                                   ATTENDANCE_REPORT_TABLE_NAME = "ATTENDANCE_REPORT_STATUS",
                                   LECTURER_ATTENDANCE_TABLE_NAME = "LECTURE_ATTENDANCE",
                                   EXAM_ATTENDANCE_TABLE_NAME = "EXAM_ATTENDANCE";
        
        
        
        protected static final String SQLITE_CREATE_STUDENTS_TABLE_SQL = "CREATE TABLE IF NOT EXISTS students "+
                        "( `student_id` VARCHAR(200) PRIMARY KEY NOT NULL,\n"
                        + "`matric_number` VARCHAR(20) NOT NULL,\n"
                        + "`parent_mobile` VARCHAR(15) NOT NULL,\n"
                        + "`full_name` VARCHAR(50) NOT NULL,\n"
                        + "`department` VARCHAR(50) NOT NULL,\n"
                        + "`level` INT(11) NOT NULL, \n"
                        + "`pic` BLOB,\n"
                        + "`finger_print_id` TEXT,  \n"
                        + "`finger_print_hash` VARCHAR(100),  \n"
                        + "`finger_print_id_left` TEXT,  \n"
                        + "`finger_print_hash_left` VARCHAR(100) \n"
                        + ")";
        protected static final String SQLITE_CREATE_STUDENT_COURSEREG_TABLE_SQL = "CREATE TABLE STUDENT_COURSE_REG "+
                        "( coursereg_id varchar(50) primary key NOT NULL, \n"
                        + "student_id varchar(200) NOT NULL,\n"
                        + "course_id varchar(50) NOT NULL \n"
                        + ")";
        
        protected static final String SQLITE_CREATE_LECTURER_TABLE_SQL = "CREATE TABLE LECTURER "+
                        "( lecturer_id varchar(50) primary key NOT NULL,\n"
                        + "full_name varchar(100) NOT NULL,\n"
                        + "email varchar(100) NOT NULL,\n"
                        + "phone char(11) NOT NULL,\n"
                        + "lecturer_title varchar(100) NOT NULL ,\n" 
                        + "department varchar(50) NOT NULL,\n"
                        + "password varchar(100) NOT NULL,\n"
                        + "pic BLOB \n"
                        + ")";

        protected static final String SQLITE_CREATE_LECTURER_COURSES_TABLE_SQL = "CREATE TABLE LECTURER_COURSES "+
                        "( course_id varchar(200) primary key NOT NULL,\n"
                        + "lecturer_id varchar(200) NOT NULL,\n"
                        + "title varchar(200) NOT NULL,\n"
                        + "credit_load int(1) NOT NULL,\n"
                        + "department varchar(200) NOT NULL,\n"
                        + "no_of_weeks int(3) NOT NULL \n"
                        + ")";
        
        protected static final String SQLITE_ATTENDANCE_REPORT_STATUS_TABLE_SQL = "CREATE TABLE ATTENDANCE_REPORT_STATUS "+
                        "( course_id varchar(200) primary key NOT NULL,\n"
                        + "week_number int(3) NOT NULL,\n"
                        + "report_status int(1) NOT NULL \n"
                        + ")";
        
        protected static final String SQLITE_CREATE_LECTURE_ATTENDANCE_TABLE_SQL = "CREATE TABLE LECTURE_ATTENDANCE "+
                        "(     `id`	INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "	`attendance_id`	varchar ( 20 ) NOT NULL UNIQUE,\n" +
                        "	`course_id`	varchar ( 10 ) NOT NULL,\n" +
                        "	`student_id`	varchar ( 10 ) NOT NULL,\n" +
                        "	`week_number`	int ( 3 ) NOT NULL,\n" +
                        "	`entry_time`	text NOT NULL"+ ")";
        
        protected static final String SQLITE_CREATE_EXAM_ATTENDANCE_TABLE_SQL = "CREATE TABLE EXAM_ATTENDANCE "+
                        "( student_id varchar(200) primary key NOT NULL,\n"
                        + "course_id varchar(200) NOT NULL,\n"
                        + "lecturer_id varchar(200) NOT NULL,\n"
                        + "entry_time text NOT NULL \n"
                        + ")";
        
}
