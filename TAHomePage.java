import java.util.ArrayList;
import java.util.Scanner;
import java.sql.*;
 
public class TAHomePage {
    static final String jdbcURL 
	= "jdbc:oracle:thin:@ora.csc.ncsu.edu:1521:orcl";

	
static Scanner userInputScanner = new Scanner(System.in);
   /*public static void main(String[] args) {
       // Initiate a new Scanner
       FirstPage();
 
       
   }*/
   
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
   
   public static void FirstPage(){
	   	System.out.print("\n1. Select Course\n2. Back\n");
	    int input = userInputScanner.nextInt();
	    if(input==1){
	   	SelectCourse();
	    }
	    else if(input==2){
	       	System.out.println("Logging out");
	       	LoginPage.delete_current_user();
	       	System.exit(0);
	    }
	    else{
	   	System.out.println("\nInvalid Input");
	   	FirstPage();
	    }
	   	
   }
   
   public static void SelectCourse(){
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
		System.out.println("No courses available");
		FirstPage();
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
   	else if(input == no_of_courses+1){
   		FirstPage();
   	}
   	else{
   		System.out.println("\nInvalid Input");
   		SelectCourse();
   	}
	}
	   }
   
   public static void CourseOptions(String c, String ctoken){	//c is the cid, ctoken is the course token
   	int is_TA=0;
   	String user="Select * from current_user";
   	ResultSet result=jdbcExecute(user);
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
	   //Check if this student exists in course_students
	   String userid="";
	   String query2="Select unityid from tassistants where unityid='"+unityid+"' and cid='"+c+"' and ctoken='"+ctoken+"'";
	   result=jdbcExecute(query2);
	   try{
			while (result.next()) {
				userid=result.getString("unityid");
				if(userid.equals(unityid) && userid!="")
					is_TA=1;
			}
			} catch(Throwable oops) {
	            oops.printStackTrace();
	        }
	   
   	if(is_TA==0)
   	{
   		System.out.println("Permission denied as you are not the TA for this course");
   		FirstPage();
   	}   
	   
   else
   {
	   
	   System.out.println("1. View homework");
	   System.out.println("2. Reports\n3. Back\n");
	   
	   int input = userInputScanner.nextInt();
	   
	   if(input == 1){
		   ViewHomework(c,ctoken);
	   }
	   else if(input == 2){
		   Reports(c,ctoken);
	   }
	   else if(input == 3){
		   SelectCourse();
	   }
	   else{
		   System.out.println("\nInvalid Input");
		   CourseOptions(c,ctoken);
	   }
   }
   }
   
   public static void ViewHomework(String c, String ctoken){
	   int no_of_hws=0;
	   ArrayList<String> hws=new ArrayList<String> ();
	   System.out.println("Available Homeworks : ");
	   String query1="Select hwid from homework where cid='"+c+"' and ctoken='"+ctoken+"'";
	   ResultSet rs=jdbcExecute(query1);
	   try{
		while (rs.next()) {
		    no_of_hws++;
		    String s=rs.getString("hwid");
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
	   
	   else if(selection==back_option)
	   {
		   CourseOptions(c,ctoken);
	   }
	   else
	   {
		   System.out.println("Invalid Input");
		   ViewHomework(c,ctoken);
	   }
	   }
	   
	   /*System.out.println("Available Homeworks:");
	   
	   int hw = userInputScanner.nextInt();
	   String hwid="dummy";
	   
	   if(hw == 1){
		   DisplayHomework(c,ctoken,hwid);
	   }
	   else if(hw == 2){
		   
	   }
	   else if(hw == 3){
		   CourseOptions(c,ctoken);
	   }
	   else{
		   System.out.println("Invalid Input");
		   ViewHomework(c,ctoken);
	   }*/
   }
   
   public static void DisplayHomework(String c, String ctoken, String hwid){
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
	   
	   /*System.out.println("HW x Details");
	   System.out.println("Press 1 to go back");
	   
	   int option = userInputScanner.nextInt();
	   
	   if(option == 1){
		   ViewHomework(c,ctoken);
	   }
	   else{
		   System.out.println("Invalid Option");
		   DisplayHomework(c,ctoken,hwid);
	   }*/
   }
   
   public static void Reports(String c, String ctoken){
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