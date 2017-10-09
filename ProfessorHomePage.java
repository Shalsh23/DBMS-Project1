import java.util.ArrayList;
import java.util.Scanner;
import java.sql.*;


 
public class ProfessorHomePage {
static Scanner userInputScanner = new Scanner(System.in);
static final String jdbcURL = "jdbc:oracle:thin:@ora.csc.ncsu.edu:1521:orcl";

public static ResultSet jdbcExecute(String query) {
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;
	   try {
        Class.forName("oracle.jdbc.driver.OracleDriver");
	    String user = "aboke";
	    String passwd = "200061305";
        try {
		conn = DriverManager.getConnection(jdbcURL, user, passwd);
		stmt = conn.createStatement();
		   rs=stmt.executeQuery(query);
		} finally {
            //close(rs);
            //close(stmt);
            //close(conn);
        }
    } catch(Throwable oops) {
        oops.printStackTrace();
    }
	return rs;
}

static void close(Connection conn) {
    if(conn != null) {
        try { conn.close(); } catch(Throwable whatever) {}
    }
}

static void close(Statement st) {
    if(st != null) {
        try { st.close(); } catch(Throwable whatever) {}
    }
}

static void close(ResultSet rs) {
    if(rs != null) {
        try { rs.close(); } catch(Throwable whatever) {}
    }
}
   /*public static void main(String[] args) {
       // Initiate a new Scanner
      
       FirstPage();      
   }*/
   
   public static void FirstPage(){
   	System.out.print("\n1. Select Course\n2. Add Course\n3. Back\nPress 0 to exit\n");
    int input = userInputScanner.nextInt();
    if(input==1){
   	SelectCourse();
    }
    else if(input==2){
    	//FirstPage();
    	AddCourse();
   	 ////////////////////////////////////////////
    }

    else if(input==0 || input==3){
   	System.out.println("Logging out");
   	LoginPage.delete_current_user();
   	System.exit(0);
    }
    else{
   	System.out.println("\nInvalid Input");
   	FirstPage();
    }
   	
   }
   
   public static void AddCourse()
   {
   	Scanner sc = new Scanner(System.in);
   	
	//Get the current user------------------------
	String userid="Select * from current_user";
	ResultSet result=jdbcExecute(userid);
	String unityid="";
	String role="";
	   try{
			while (result.next()) {
				unityid=result.getString("unityid");
				role=result.getString("role");
			}
			} catch(Throwable oops) {
	            oops.printStackTrace();
	        }
	   
	   
   	System.out.println("Enter the course id");
   	String cid=sc.nextLine();
   	System.out.println("Enter the course token");
   	String ctoken=sc.nextLine();
   	String query="Select cid from Courses";
   	ResultSet res;
   	res=jdbcExecute(query);
   	int exists=0;
   	try{
   	while(res.next())
   	{
   		String s = res.getString("cid");
   		if(s.equals(cid))
   		{
   			exists=1;
   			break;
   		}
   	}
   	}catch(Throwable oops) {
        oops.printStackTrace();
   	}//end catch
   	
   	if(exists==0)
   	{
   		System.out.println("Invalid ID");
   		FirstPage();
   	}
   	else{
   		try {
   	       Class.forName("oracle.jdbc.driver.OracleDriver");
   		    String user = "aboke";	
   		    String passwd = "200061305";
   	            Connection conn = null;
   	            Statement stmt = null;
   	            ResultSet rs = null;
   	            try {
   			conn = DriverManager.getConnection(jdbcURL, user, passwd);
   			stmt = conn.createStatement();
   			stmt.executeUpdate("INSERT INTO COURSE_INSTRUCTORS VALUES('"+unityid+"','"+cid+"','"+ctoken+"')");
   			}finally {}
   	}catch(Throwable oops) {
   	            oops.printStackTrace();
   	            }
   			System.out.print("Course Added successfully\n");
   	}
   	//System.out.print(ctoken);
   	
   }
   public static void SelectCourse(){
   	//System.out.println("1. CSC540-Database Management Systems\n2. Back\n");
	   ResultSet rs;
	   int no_of_courses=0;
	   //String[] courses=new String[100];
	   ArrayList<String> courses=new ArrayList<String>();
	   ArrayList<String> ctokens=new ArrayList<String>();
	   String query="Select co.cid, c.cname, co.ctoken from course_offerings co, courses c where c.cid=co.cid";
	   rs=jdbcExecute(query);
	   try{
		while (rs.next()) {
		    String s = rs.getString("cid");
		    String n = rs.getString("cname");
		    String token=rs.getString("ctoken");
		    no_of_courses++;
		    System.out.println(no_of_courses+". " + s + "   " + n + "     "+ token);
		    courses.add(s);
		    ctokens.add(token);
		}
		} catch(Throwable oops) {
            oops.printStackTrace();
        }
	   if(no_of_courses==0)
	   {
		   System.out.println("No eligible courses available");
	   }
	   else
	   {
			int back_option=no_of_courses+1;
			System.out.println(back_option+ ". Enter "+back_option+" to Go Back");
   	int input = userInputScanner.nextInt();
   	
   	if(input<=no_of_courses){
   		System.out.println("Course chosen :"+ courses.get(input-1)+ctokens.get(input-1));
   		CourseOptions(courses.get(input-1),ctokens.get(input-1));
   	}
   	else if(input==back_option){
   		FirstPage();
   	}
   	else{
   		System.out.println("Invalid Input");
   		SelectCourse();
   	}
	   }
   }
   
   public static void CourseOptions(String c, String ctoken){
	   System.out.println("Course argument received" + c);
	   System.out.println("1. Add homework\n2. Add/Remove questions to homework");
	   System.out.println("3. Edit homework\n4. View homework");
	   System.out.println("5. View Notification\n6. Reports\n7. Back\n");
	   
	   int input = userInputScanner.nextInt();
	   
	   if(input == 1){
		   AddHomework(c,ctoken);		// c here is the course id for which the homework is to be added
	   }
	   else if(input == 2){
		   AddRemoveQuestions(c,ctoken);		// c here is the course id for which we will need to display the list of all the homeworks available for that course c
	   }
	   else if(input == 3){
		   EditHomework(c,ctoken);
	   }
	   else if(input == 4){
		   ViewHomework(c,ctoken);
	   }
	   else if(input == 5){
		   ViewNotification(c,ctoken);
	   }
	   else if(input == 6){
		   Reports(c,ctoken);
	   }
	   else if(input == 7){
		   SelectCourse();
	   }
	   else{
		   System.out.println("\nInvalid Input");
		   CourseOptions(c,ctoken);
	   }
   }
   
   public static void AddHomework(String c, String ctoken){		//c here is the course id for which the HW is to be added
	   
	   Scanner sc = new Scanner(System.in);
	   System.out.println("Start Date : \n");
	   String startDate = userInputScanner.next();
	   
	   System.out.println("End Date : \n");
	   String endDate = userInputScanner.next();
	   
	   System.out.println("Number Of Attempts : \n");
	   int numOfAttempts = userInputScanner.nextInt();
	   
	   System.out.println("Topics : \n");
	   String topics = sc.nextLine();
	   
	   System.out.println("Difficulty Range : \n");
	   int range = userInputScanner.nextInt();
	   
	   System.out.println("Score Selection Scheme : \n");
	   String scheme = sc.nextLine();
	   
	   System.out.println("Number Of Questions : \n");
	   int numOfQuestions = userInputScanner.nextInt();
	   
	   System.out.println("Correct Answer Points : \n");
	   int correctPoints = userInputScanner.nextInt();
	   
	   System.out.println("Incorrect Answer Points : \n");
	   int incorrectPoints = userInputScanner.nextInt();
	   try {
   	       Class.forName("oracle.jdbc.driver.OracleDriver");
   		    String user = "aboke";	
   		    String passwd = "200061305";
   	            Connection conn = null;
   	            Statement stmt = null;
   	            ResultSet rs = null;
   	            try {
   			conn = DriverManager.getConnection(jdbcURL, user, passwd);
   			stmt = conn.createStatement();
   			String hw_counts="Select COUNT(*) as hw_count from homework";
   			ResultSet hw_cnt;
   			int hw_id=0;
   			hw_cnt=jdbcExecute(hw_counts);
   			while(hw_cnt.next())
   			{
   			hw_id=hw_cnt.getInt("hw_count");
   			}
   			hw_id=hw_id+1;
   			String topic_id="Select tid from topics where tname='"+topics+"'";
   			ResultSet tid=jdbcExecute(topic_id);
   			int id=-1;
   		   try{
   			while (tid.next()) {
   				id=tid.getInt("tid");
   			}
   			} catch(Throwable oops) {
   	            oops.printStackTrace();
   	        }
   			String hw_add_query="INSERT INTO HOMEWORK VALUES("+hw_id+",'"+c+"','"+ctoken+"','"+startDate+"','"+endDate+"',"+numOfAttempts+",'"+scheme+"',"+range+","+correctPoints+","+incorrectPoints+","+numOfQuestions+","+id+")";
   			System.out.println(hw_add_query);
   			stmt.executeUpdate(hw_add_query);
   			}finally {}
   	}catch(Throwable oops) {
   	            oops.printStackTrace();
   	            }
	   
	   System.out.println("HW Added!!\n");
	   
	   CourseOptions(c,ctoken);
   }
   
   public static void AddRemoveQuestions(String c, String ctoken){
	   //System.out.println("1.HW1\n2.HW2\n3.HW3\n4.Back\n");
	   int no_of_hws=0;
	   ArrayList<String> hws=new ArrayList<String>();
	   String query1="Select hwid from homework where cid='"+c+"' and ctoken='"+ctoken+"' order by hwid";
	   ResultSet rs=jdbcExecute(query1);
	   try{
		while (rs.next()) {
		    String s = rs.getString("hwid");
		    no_of_hws++;
		    System.out.println(Integer.toString(no_of_hws)+'\t'+"HW "+s);
		    hws.add(s);
		}
		} catch(Throwable oops) {
            oops.printStackTrace();
        }
	   System.out.println("Enter 0 to Go Back");
	   if(no_of_hws==0)
	   {
		   System.out.println("No active homeworks yet");
		   CourseOptions(c,ctoken);
	   }
	   else
	   {
		   System.out.println("Select a Homework:\n");
	   int selection = userInputScanner.nextInt();
	   
	   //hwid will be derived here after the user input
	   //for now, initializing hwid to be dummy
	
	   if(selection ==0){
		   CourseOptions(c,ctoken);
	   }
	   else if(selection <= no_of_hws && selection !=0){
		   //String hwid=Integer.toString(selection);
		   String hwid=hws.get(selection-1);
		   UpdateHomework(c,ctoken,hwid);			//hwid is the id of the HW which is to be edited
	   }
	   else
	   {
		   System.out.println("Invalid Input");
		   AddRemoveQuestions(c,ctoken);
	   }
	   }
   }
   
   public static void UpdateHomework(String c, String ctoken, String hwid){	//c is the course id , hwid is the hw id of the homework which is to be updated
	   System.out.println("1. Search and Add question\n2. Remove question\n3. Back");
	   int selection = userInputScanner.nextInt();
	   
	   if(selection == 1){
		   AddQuestion(c,ctoken,hwid);
		   UpdateHomework(c,ctoken,hwid);
	   }
	   else if(selection == 2){
		   RemoveQuestion(c,ctoken,hwid);
		   UpdateHomework(c,ctoken,hwid);
	   }
	   else if(selection == 3){
		   AddRemoveQuestions(c,ctoken);
		   //UpdateHomework(c,ctoken,hwid);
	   }
	   else{
		   System.out.println("Invalid Input");
		   UpdateHomework(c,ctoken,hwid);
	   }
   }
   
   public static void AddQuestion(String c, String ctoken, String hwid){
	   //Get the topic_id of the homework:
	   int hw_id=Integer.parseInt(hwid);
	   String topic_hw="Select difficultyrange,tid from homework where hwid="+hw_id;
	   ResultSet id=jdbcExecute(topic_hw);
	   int tid=-1;
	   int difflevel=-1;
	   try{
		while (id.next()) {
			tid=id.getInt("tid");
			difflevel=id.getInt("difficultyrange");
		}
		} catch(Throwable oops) {
            oops.printStackTrace();
        }
	   
	   //Retrieve all the questions:
	   ArrayList<String> questions= new ArrayList<String>();
	   String ques="Select ques from questions where difflevel="+difflevel+" and topic_id="+tid;
	   id=jdbcExecute(ques);
	   int q_no=0;
	   try{
		while (id.next()) {
			q_no++;
			String aa=id.getString("ques");
			questions.add(aa);
		}
		} catch(Throwable oops) {
            oops.printStackTrace();
        }
	   if(q_no==0)
	   {
		   System.out.println("No questions available pertaining to the difficulty level and the topic(s) of the homework");
	   }
	   else
	   {
	   System.out.println("These are the questions available:");
	   System.out.println("Select the question to be added");
	   int i=0;
	   for(i=0;i<q_no;i++)
	   {
		   System.out.println(Integer.toString(i+1)+". "+questions.get(i));
	   }
	   int input=userInputScanner.nextInt();
	   if(input<=q_no)
	   {
		   //Insert in HW-Qns
		   String qid="Select qid from questions where ques='"+questions.get(input-1)+"'";
		   id=jdbcExecute(qid);
		   int id2=0;
		   try{
				while (id.next()) {
					id2=id.getInt("qid");	//id2 has the question id
				}
				} catch(Throwable oops) {
		            oops.printStackTrace();
		        }
		   try {
	   	       Class.forName("oracle.jdbc.driver.OracleDriver");
	   		    String user = "aboke";	
	   		    String passwd = "200061305";
	   	            Connection conn = null;
	   	            Statement stmt = null;
	   	            ResultSet rs = null;
	   	            try {
	   			conn = DriverManager.getConnection(jdbcURL, user, passwd);
	   			stmt = conn.createStatement();
	   			String add="INSERT INTO HW_QUESTIONS VALUES ("+hw_id+","+id2+")";
	   			stmt.executeUpdate(add);
	   			}finally {
	   				close(conn);
	   				close(stmt);
	   				close(rs);
	   			}
	   	}catch(Throwable oops) {
	   	            //oops.printStackTrace();
	   				System.out.println("Question already present in the Homework");
	   	            }
	   			System.out.print("Question Added successfully\n");
	   	}	
	   else
	   {
		   System.out.println("Invalid Input");
		   return;
	   }
	   }
	   
   }
   
   public static void RemoveQuestion(String c, String ctoken, String hwid){
	   int hw_id=Integer.parseInt(hwid);
	   //Retrieve all the questions already there for the homework
	   int no_questions=0;
	   ArrayList<String> questions= new ArrayList<String>();
	   String query="Select q.ques from questions q, hw_questions hq where hq.qid=q.qid and hq.hwid="+hw_id;
	   ResultSet result=jdbcExecute(query);
	   try{
			while (result.next()) {
				String q=result.getString("ques");	//id2 has the question id
				questions.add(q);
				no_questions++;
			}
			} catch(Throwable oops) {
	            oops.printStackTrace();
	        }
	   if(no_questions==0)
	   {
		   System.out.println("No questions available in the homework");
	   }
	   else
	   {
	   System.out.println("These are the questions available:");
	   System.out.println("Choose a question to remove:");
	   int i=0;
	   for(i=0;i<no_questions;i++)
	   {
		   System.out.println(Integer.toString(i+1)+". "+questions.get(i));
	   }
	   int input=userInputScanner.nextInt();
	   if(input<=no_questions)
	   {
		   String qid="Select qid from questions where ques='"+questions.get(input-1)+"'";
		   ResultSet id;
		   id=jdbcExecute(qid);
		   int id2=0;
		   try{
				while (id.next()) {
					id2=id.getInt("qid");	//id2 has the question id
				}
				} catch(Throwable oops) {
		            oops.printStackTrace();
		        }
		   String remove_question="DELETE FROM HW_QUESTIONS WHERE hwid="+hw_id+" and qid="+id2;
	   		try {
		   	       Class.forName("oracle.jdbc.driver.OracleDriver");
		   		    String user = "aboke";	
		   		    String passwd = "200061305";
		   	            Connection conn = null;
		   	            Statement stmt = null;
		   	            ResultSet rs11 = null;
		   	            try {
		   			conn = DriverManager.getConnection(jdbcURL, user, passwd);
		   			stmt = conn.createStatement();
		   			stmt.executeUpdate(remove_question);
		   			}finally {
		   				close(conn);
		   				close(rs11);
		   				close(stmt);
		   			}
		   	}catch(Throwable oops) {
		   	            oops.printStackTrace();
		   	            } 
	   }
	   
	   else
	   {
		   System.out.println("Invalid Input");
		   return;
	   }
	   }
	   
   }
   
   public static void EditHomework(String c, String ctoken){
	   int no_of_hws=0;
	   String query1="Select hwid from homework where cid='"+c+"' and ctoken='"+ctoken+"' order by hwid";
	   ResultSet rs=jdbcExecute(query1);
	   ArrayList<String> hws=new ArrayList<String>();
	   try{
		while (rs.next()) {
		    String s = rs.getString("hwid");
		    no_of_hws++;
		    System.out.println(Integer.toString(no_of_hws)+'\t'+"HW "+s);
		    hws.add(s);
		}
		} catch(Throwable oops) {
            oops.printStackTrace();
        }
	   System.out.println("Enter 0 to Go Back");
	   if(no_of_hws==0)
	   {
		   System.out.println("No active homeworks yet");
		   CourseOptions(c,ctoken);
	   }
	   else
	   {
		   System.out.println("Select a Homework:\n");
	   int selection = userInputScanner.nextInt();
	   
	   //hwid will be derived here after the user input
	   //for now, initializing hwid to be dummy
	
	   if(selection ==0){
		   CourseOptions(c,ctoken);
	   }
	   else if(selection <= no_of_hws && selection !=0){
		   //String hwid=Integer.toString(selection);
		   String hwid=hws.get(selection-1);
		   ModifyHomework(c,ctoken,hwid);			//hwid is the id of the HW which is to be edited
	   }
	   else
	   {
		   System.out.println("Invalid Input");
		   EditHomework(c,ctoken);
	   }
	   }
	   
	   //Similarly to EditHomework, hwid is the id of the homework which is to be modified
	   //This will be derived here
   }
   
   public static void ModifyHomework(String c, String ctoken, String hwid){	//c is the course id, hwid is the homework to be modified
	   System.out.println("Choose what to update:");
	   System.out.println("1. Start date");
	   System.out.println("2. End date");
	   System.out.println("3. Number of attempts");
	   System.out.println("4. Topics");
	   System.out.println("5. Difficulty level");
	   System.out.println("6. Score selection");
	   System.out.println("7. Number of questions");
	   System.out.println("8. Correct answer points");
	   System.out.println("9. Incorrect answer points");
	   System.out.println("10. Back");
	   
	   int selection = userInputScanner.nextInt();
	   
	   if(selection == 1){
		   UpdateStartDate(c,ctoken,hwid);
	   }
	   else if(selection == 2){
		   UpdateEndDate(c,ctoken,hwid);
	   }
	   else if(selection == 3){
		   UpdateNumberOfAttempts(c,ctoken,hwid);
	   }
	   else if(selection == 4){
		   UpdateTopics(c,ctoken,hwid);
	   }
	   else if(selection == 5){
		   UpdateDifficultyLevel(c,ctoken,hwid);
	   }
	   
	   else if(selection == 6){
		   UpdateScoreSelection(c,ctoken,hwid);
	   }
	   else if(selection == 7){
		   UpdateNumOfQuestions(c,ctoken,hwid);
	   }
	   else if(selection == 8){
		   UpdateCorrectAnsPoints(c,ctoken,hwid);
	   }
	   else if(selection == 9){
		   UpdateIncorrectAnsPoints(c,ctoken,hwid);
	   }
	   else if(selection == 10){
		   CourseOptions(c,ctoken);
	   }
	   else{
		   System.out.println("Invalid Input");
		   ModifyHomework(c,ctoken,hwid);
	   }
   }
   
   public static void UpdateStartDate(String c, String ctoken,String hwid){	//c is the courseid, hwid is the homework id
	   System.out.println("Enter new Start Date");
	   Scanner sc = new Scanner(System.in);
	   String startDate = sc.next();
	   int hw_id=Integer.parseInt(hwid);
	   String query="UPDATE HOMEWORK SET startdate='"+startDate+"' where hwid="+hw_id;
	   try {
   	       Class.forName("oracle.jdbc.driver.OracleDriver");
   		    String user = "aboke";	
   		    String passwd = "200061305";
   	            Connection conn = null;
   	            Statement stmt = null;
   	            ResultSet rs = null;
   	            try {
   			conn = DriverManager.getConnection(jdbcURL, user, passwd);
   			stmt = conn.createStatement();
   			stmt.executeUpdate(query);
   			}finally {close(conn);
   						close(stmt);
   						close(rs);
   						}
   	}catch(Throwable oops) {
   	            oops.printStackTrace();
   	            }
	   System.out.println("Start Date of HW"+hw_id+" changed successfully to "+startDate);
	   
	   ModifyHomework(c,ctoken,hwid);
   }
   
   public static void UpdateEndDate(String c, String ctoken,String hwid){		//c is the courseid, hwid is the homework id
	   System.out.println("Enter new End Date");
	   Scanner sc = new Scanner(System.in);
	   String endDate = sc.next();
	   int hw_id=Integer.parseInt(hwid);
	   String query="UPDATE HOMEWORK SET enddate='"+endDate+"' where hwid="+hw_id;
	   try {
   	       Class.forName("oracle.jdbc.driver.OracleDriver");
   		    String user = "aboke";	
   		    String passwd = "200061305";
   	            Connection conn = null;
   	            Statement stmt = null;
   	            ResultSet rs = null;
   	            try {
   			conn = DriverManager.getConnection(jdbcURL, user, passwd);
   			stmt = conn.createStatement();
   			stmt.executeUpdate(query);
   			}finally {close(conn);
   						close(stmt);
   						close(rs);
   						}
   	}catch(Throwable oops) {
   	            oops.printStackTrace();
   	            }
	   System.out.println("End Date of HW"+hw_id+" changed successfully to "+endDate);
	   ModifyHomework(c,ctoken,hwid);
   }
   
   public static void UpdateNumberOfAttempts(String c, String ctoken,String hwid){	//c is the hwid, hwid is the homework id
	   System.out.println("Enter new Number OF Attempts");
	   int numOfAttempts = userInputScanner.nextInt();
	   int hw_id=Integer.parseInt(hwid);
	   String query="UPDATE HOMEWORK SET numofretries="+numOfAttempts+" where hwid="+hw_id;
	   try {
   	       Class.forName("oracle.jdbc.driver.OracleDriver");
   		    String user = "aboke";	
   		    String passwd = "200061305";
   	            Connection conn = null;
   	            Statement stmt = null;
   	            ResultSet rs = null;
   	            try {
   			conn = DriverManager.getConnection(jdbcURL, user, passwd);
   			stmt = conn.createStatement();
   			stmt.executeUpdate(query);
   			}finally {close(conn);
   						close(stmt);
   						close(rs);
   						}
   	}catch(Throwable oops) {
   	            oops.printStackTrace();
   	            }
	   System.out.println("Number of attempts of HW"+hw_id+" changed successfully to "+numOfAttempts);
	   ModifyHomework(c,ctoken,hwid);
   }
   
   public static void UpdateTopics(String c, String ctoken,String hwid){	//c is the hwid, hwid is the homework id
	   System.out.println("Enter new Number OF Attempts");
	   String topics = userInputScanner.next();
	   
	   ModifyHomework(c,ctoken,hwid);
   }
   
   public static void UpdateDifficultyLevel(String c, String ctoken,String hwid){	//c is the hwid, hwid is the homework id
	   System.out.println("Enter new Difficulty Level");
	   int difficultyLevel = userInputScanner.nextInt();
	   int hw_id=Integer.parseInt(hwid);
	   String query="UPDATE HOMEWORK SET difficultyrange="+difficultyLevel+" where hwid="+hw_id;
	   try {
   	       Class.forName("oracle.jdbc.driver.OracleDriver");
   		    String user = "aboke";	
   		    String passwd = "200061305";
   	            Connection conn = null;
   	            Statement stmt = null;
   	            ResultSet rs = null;
   	            try {
   			conn = DriverManager.getConnection(jdbcURL, user, passwd);
   			stmt = conn.createStatement();
   			stmt.executeUpdate(query);
   			}finally {close(conn);
   						close(stmt);
   						close(rs);
   						}
   	}catch(Throwable oops) {
   	            oops.printStackTrace();
   	            }
	   System.out.println("Difficulty level of HW"+hw_id+" changed successfully to "+difficultyLevel);
	   
	   ModifyHomework(c,ctoken,hwid);
   }
   
   public static void UpdateScoreSelection(String c, String ctoken,String hwid){	//c is the hwid, hwid is the homework id
	   System.out.println("Enter new Score selection scheme");
	   Scanner sc = new Scanner(System.in);
	   String scoreSelection = sc.next();
	   int hw_id=Integer.parseInt(hwid);
	   String query="UPDATE HOMEWORK SET selectionmethod='"+scoreSelection+"' where hwid="+hw_id;
	   try {
   	       Class.forName("oracle.jdbc.driver.OracleDriver");
   		    String user = "aboke";	
   		    String passwd = "200061305";
   	            Connection conn = null;
   	            Statement stmt = null;
   	            ResultSet rs = null;
   	            try {
   			conn = DriverManager.getConnection(jdbcURL, user, passwd);
   			stmt = conn.createStatement();
   			stmt.executeUpdate(query);
   			}finally {close(conn);
   						close(stmt);
   						close(rs);
   						}
   	}catch(Throwable oops) {
   	            oops.printStackTrace();
   	            }
	   System.out.println("Selection method of HW"+hw_id+" changed successfully to "+scoreSelection);
	   
	   ModifyHomework(c,ctoken,hwid);
   }
   
   public static void UpdateNumOfQuestions(String c,String ctoken, String hwid){	//c is the hwid, hwid is the homework id
	   System.out.println("Enter new Number OF Questions");
	   int numOfQuestions = userInputScanner.nextInt();
	   int hw_id=Integer.parseInt(hwid);
	   String query="UPDATE HOMEWORK SET numofquestions="+numOfQuestions+" where hwid="+hw_id;
	   try {
   	       Class.forName("oracle.jdbc.driver.OracleDriver");
   		    String user = "aboke";	
   		    String passwd = "200061305";
   	            Connection conn = null;
   	            Statement stmt = null;
   	            ResultSet rs = null;
   	            try {
   			conn = DriverManager.getConnection(jdbcURL, user, passwd);
   			stmt = conn.createStatement();
   			stmt.executeUpdate(query);
   			}finally {close(conn);
   						close(stmt);
   						close(rs);
   						}
   	}catch(Throwable oops) {
   	            oops.printStackTrace();
   	            }
	   System.out.println("Number of questions for HW"+hw_id+" changed successfully to "+numOfQuestions);
	   
	   ModifyHomework(c,ctoken,hwid);
   }
   
   public static void UpdateCorrectAnsPoints(String c, String ctoken,String hwid){	//c is the hwid, hwid is the homework id
	   System.out.println("Enter new Correct answer points");
	   int correctPoints = userInputScanner.nextInt();
	   int hw_id=Integer.parseInt(hwid);
	   String query="UPDATE HOMEWORK SET PointsForCorrectQues="+correctPoints+" where hwid="+hw_id;
	   try {
   	       Class.forName("oracle.jdbc.driver.OracleDriver");
   		    String user = "aboke";	
   		    String passwd = "200061305";
   	            Connection conn = null;
   	            Statement stmt = null;
   	            ResultSet rs = null;
   	            try {
   			conn = DriverManager.getConnection(jdbcURL, user, passwd);
   			stmt = conn.createStatement();
   			stmt.executeUpdate(query);
   			}finally {close(conn);
   						close(stmt);
   						close(rs);
   						}
   	}catch(Throwable oops) {
   	            oops.printStackTrace();
   	            }
	   System.out.println("Points per correct answer for HW"+hw_id+" changed successfully to "+correctPoints);
	   ModifyHomework(c,ctoken,hwid);
   }
   
   public static void UpdateIncorrectAnsPoints(String c, String ctoken,String hwid){
	   System.out.println("Enter new incorrect answer points");
	   int incorrectPoints = userInputScanner.nextInt();
	   int hw_id=Integer.parseInt(hwid);
	   String query="UPDATE HOMEWORK SET PointsForIncorrectQues="+incorrectPoints+" where hwid="+hw_id;
	   try {
   	       Class.forName("oracle.jdbc.driver.OracleDriver");
   		    String user = "aboke";	
   		    String passwd = "200061305";
   	            Connection conn = null;
   	            Statement stmt = null;
   	            ResultSet rs = null;
   	            try {
   			conn = DriverManager.getConnection(jdbcURL, user, passwd);
   			stmt = conn.createStatement();
   			stmt.executeUpdate(query);
   			}finally {close(conn);
   						close(stmt);
   						close(rs);
   						}
   	}catch(Throwable oops) {
   	            oops.printStackTrace();
   	            }
	   System.out.println("Points per incorrect answer for HW"+hw_id+" changed successfully to "+incorrectPoints);
	   ModifyHomework(c,ctoken,hwid);
   }
   
   public static void ViewHomework(String c,String ctoken){	//c is the course id
	   int no_of_hws=0;
	   ArrayList<String> hws=new ArrayList<String>();
	   String query1="Select hwid from homework where cid='"+c+"' and ctoken='"+ctoken+"' order by hwid";
	   ResultSet rs=jdbcExecute(query1);
	   try{
		while (rs.next()) {
		    String s = rs.getString("hwid");
		    no_of_hws++;
		    System.out.println(Integer.toString(no_of_hws)+'\t'+"HW "+s);
		    hws.add(s);
		    
		}
		} catch(Throwable oops) {
            oops.printStackTrace();
        }
	   //System.out.println("Enter 0 to Go Back");
	   if(no_of_hws==0)
	   {
		   System.out.println("No active homeworks yet");
		   CourseOptions(c,ctoken);
	   }
	   else
	   {
		   int back_option=no_of_hws+1;
		   System.out.println(back_option+"\tEnter "+back_option+" to Go Back");
		   System.out.println("Select a Homework:\n");
		   int selection = userInputScanner.nextInt();
	   
	   //hwid will be derived here after the user input
	   //for now, initializing hwid to be dummy
	
	   if(selection ==0){
		   CourseOptions(c,ctoken);
	   }
	   else if(selection <= no_of_hws && selection !=0){
		   //String hwid=Integer.toString(selection);
		   String hwid=hws.get(selection-1);
		   DisplayHomework(c,ctoken,hwid);			//hwid is the id of the HW which is to be edited
	   }
	   else if(selection==no_of_hws+1)
	   {
		   CourseOptions(c,ctoken);
	   }
	   else
	   {
		   System.out.println("Invalid Input");
		   ViewHomework(c,ctoken);
	   }
	   }
   }
   
   public static void DisplayHomework(String c, String ctoken,String hwid){		//c is the course id, hwid is the homework id of the homework to be displayed
	   int hw_id=Integer.parseInt(hwid);
	   System.out.println("HW"+hw_id+" Details");
	   String query1="Select * from homework where hwid="+hw_id;
	   ResultSet rs=jdbcExecute(query1);
	   try{
		while (rs.next()) {
			int hw=rs.getInt("hwid");
		    String s1 = rs.getString("startdate");
		    String s2 = rs.getString("enddate");
		    int no_of_questions=rs.getInt("numofquestions");
		    int no_of_attempts=rs.getInt("numofretries");
		    int difficulty_level=rs.getInt("difficultyrange");
		    int points_correct=rs.getInt("pointsforcorrectques");
		    int points_incorrect=rs.getInt("pointsforincorrectques");
		    String s3 = rs.getString("selectionmethod");
		    System.out.println("HW Name : HW"+hw);
		    System.out.println("HW Start Date :"+s1);
		    System.out.println("HW End Date :"+s2);
		    System.out.println("No. of questions in the homework :"+no_of_questions);
		    System.out.println("No. of attempts allowed: "+no_of_attempts);
		    System.out.println("Difficulty level of the homework as set by the instructor :"+difficulty_level);
		    System.out.println("Points per correct answer for this homework :"+points_correct);
		    System.out.println("Points per incorrect answer for this homework :"+points_incorrect);
		    System.out.println("Score selection method for this homework :"+s3);
		}
		} catch(Throwable oops) {
            oops.printStackTrace();
        }
	   
	   System.out.println("Question list :");
	   //------------------------------------------Updates needed here------------------------
	   //Retrieve all the questions already there for the homework
	   int no_questions=0;
	   ArrayList<String> questions= new ArrayList<String>();
	   String query="Select q.ques from questions q, hw_questions hq where hq.qid=q.qid and hq.hwid="+hw_id;
	   ResultSet result=jdbcExecute(query);
	   try{
			while (result.next()) {
				String q=result.getString("ques");	//id2 has the question id
				questions.add(q);
				no_questions++;
			}
			} catch(Throwable oops) {
	            oops.printStackTrace();
	        }
	   if(no_questions==0)
	   {
		   System.out.println("No questions available in the homework");
	   }
	   else
	   {
	   int i=0;
	   for(i=0;i<no_questions;i++)
	   {
		   System.out.println(Integer.toString(i+1)+". "+questions.get(i));
	   }
	   }
	   //------------------------------------Questions listing done here
	   System.out.println("");
	   System.out.println("Press 1 to go back");
	   
	   int option = userInputScanner.nextInt();
	   
	   if(option == 1){
		   ViewHomework(c,ctoken);
	   }
	   else{
		   System.out.println("Invalid Option");
		   DisplayHomework(c,ctoken,hwid);
	   }
   }
   
   public static void ViewNotification(String c,String ctoken){	//c is the course id ( currently running)
	   System.out.println("Notification Details");
	   System.out.println("Press 1 to go back");
	   
	   int option = userInputScanner.nextInt();
	   
	   if(option == 1){
		   CourseOptions(c,ctoken);
	   }
	   else{
		   System.out.println("Invalid Option");
		   ViewNotification(c,ctoken);
	   }
   }
   
   public static void Reports(String c,String ctoken){	//c is the course id (currently running)
	   System.out.println("Enter your query in SQL or enter 'back' to go back");
	   Scanner sc=new Scanner(System.in);
	   String query = sc.nextLine();
	   if(!query.equals("back"))
	   {
	   ResultSet rs=jdbcExecute(query);
	   int numOfCols=0;
	   try{
	   ResultSetMetaData rsmd = rs.getMetaData();
	   numOfCols = rsmd.getColumnCount();
	   }catch(Throwable oops) {
           oops.printStackTrace();
       }
	   
	   String q="";
	   System.out.println("Results");
	   try{
			while (rs.next()) {
				for(int i = 1; i <= numOfCols; i++)
				{
					q=rs.getString(i);
					System.out.print(q+'\t');
				}
				System.out.println("");
			}
			} catch(Throwable oops) {
	            oops.printStackTrace();
	        }
	   
	   Reports(c,ctoken);
   }
	   
	   if(query.equals("back")){
		   CourseOptions(c,ctoken);
	   }
   }
}