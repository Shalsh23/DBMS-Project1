import java.util.ArrayList;
import java.util.Scanner;
import java.sql.*;
 
public class LoginPage {
static Scanner userInputScanner = new Scanner(System.in);
static final String jdbcURL = "jdbc:oracle:thin:@ora.csc.ncsu.edu:1521:orcl";
   public static void main(String[] args) {
       // Initiate a new Scanner
	   delete_current_user();
       FirstPage();   
   }
   
   public static void delete_current_user()
   {
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
	   
	   userid="DELETE FROM CURRENT_USER WHERE unityid='"+unityid+"'";
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
		   			stmt.executeUpdate(userid);
		   			}finally {
		   				close(conn);
		   				close(rs11);
		   				close(stmt);
		   			}
		   	}catch(Throwable oops) {
		   	            oops.printStackTrace();
		   	            }   
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
   
   public static void FirstPage(){
	   	System.out.print("\nSelect an option\n1. Login\n2. Create User\n3. Exit\n");
	    int input = userInputScanner.nextInt();
	    if(input==1){
	    	LoginPage();
	    }
	    else if(input==2){
	    	CreateUser();
	    }
	    else if(input == 3){
	    	//Delete the current_user record
	    	System.exit(0);
	    }
	    else{
	   	System.out.println("\nInvalid Input");
	   	FirstPage();
	    }
	   	
  }
   
   public static void LoginPage(){
	   System.out.println("Enter the username");
	   Scanner sc=new Scanner(System.in);
	   String username=sc.nextLine();
	   System.out.println("Enter the password for authentication");
	   String pwd=sc.nextLine();
	   System.out.print("\nSelect User Type\n1. Student\n2. Instructor\n3. Teaching Assistant\n");
	   int input = userInputScanner.nextInt();
	   String role="";
	   if(input==1){
		   role="Student";
	   }
	   else if(input==2)
	   {
		   role="Instructor";
	   }
	   else if(input==3)
	   {
		   role="Teaching Assistant";
	   }
	   else{}
	   
	   String query="Select unityid from users where unityid='"+username+"'";
	   ResultSet rs=jdbcExecute(query);
	   String uid="";
	   try{
		while (rs.next()) {
			uid=rs.getString("unityid");
		}
		} catch(Throwable oops) {
            oops.printStackTrace();
        }
	   if(uid.equals(username))
	   {
		   //Check pwd now
		   String pwd_check="Select password from users where unityid='"+username+"'";
		   String pwd_returned="";
		   rs=jdbcExecute(pwd_check);
		   try{
				while (rs.next()) {
					pwd_returned=rs.getString("password");
				}
				} catch(Throwable oops) {
		            oops.printStackTrace();
		        }
		   if(pwd_returned.equals(pwd))
		   {
			   //Role check
			   String role_check="Select role from users_roles where unityid='"+username+"'";
			   int correct_role=0;
			   String role_returned="";
			   rs=jdbcExecute(role_check);
			   try{
					while (rs.next()) {
						role_returned=rs.getString("role");
						if(role_returned.equals(role)){
							correct_role=1;
						}
					}
					} catch(Throwable oops) {
			            oops.printStackTrace();
			        }
			   if(correct_role==1)
			   {
				   //Inserting this user in current_user
				   try {
		    	       Class.forName("oracle.jdbc.driver.OracleDriver");
		    		    String user = "aboke";	
		    		    String passwd = "200061305";
		    	            Connection conn = null;
		    	            Statement stmt = null;
		    	            ResultSet test = null;
		    	            try {
		    			conn = DriverManager.getConnection(jdbcURL, user, passwd);
		    			String query_current_user="INSERT INTO CURRENT_USER VALUES('"+username+"','"+role+"')";
		    			stmt = conn.createStatement();
		    			stmt.executeUpdate(query_current_user);
		    			}finally {close(conn);
		    			close(stmt);
		    			close(rs);
		    			}
		    	}catch(Throwable oops) {
		    	            oops.printStackTrace();
		    	            }
				   
				   if(input==1)
				   {
					   StudentHomePage.StudentMain();
					   delete_current_user();
				   }
			else if(input==2){
				
		    	ProfessorHomePage.FirstPage();
		    	delete_current_user();
		    }
		    else if(input==3){
		    	TAHomePage.FirstPage();
		    	delete_current_user();
		    }
		    else{
		   	System.out.println("\nInvalid Input");
		   	FirstPage();
		    }
			}
			   else
			   {
				   System.out.println("Role entered for username : "+username+" not found");
				   FirstPage();
			   }
		   }
		   else
		   {
			   System.out.println("Incorrect password for authentication");
			   FirstPage();
		   }
	   }
	   else
	   {
		   System.out.println("Invalid Username");
		   FirstPage();
	   }
   }
   
   public static void CreateUser(){
	   Scanner sc=new Scanner(System.in);
	   	System.out.print("\nEnter new Username\n");
	    String username = sc.next();
	    
	    System.out.print("Enter new Password\n");
	    String password = sc.next();
	    
	    System.out.println("Enter the first name");
	    String fname=sc.next();
	    
	    System.out.println("Enter the last name");
	    String lname=sc.next();
	    
		System.out.print("\nSelect User Type\n1. Student\n2. Instructor\n3. Teaching Assistant\n");
		int input = userInputScanner.nextInt();
		String role="";
		String query_is_a_users="";
		if(input==1){
			   role="Student";
			   System.out.println("Select the education level of the student");
			   System.out.println("1. Undergraduate\n2. Graduate");
			   String ed_level="";
			   int in=userInputScanner.nextInt();
			   if(in==1)
			   {
				   ed_level="Undergraduate";
			   }
			   else if(in==2)
			   {
				   ed_level="Graduate";
			   }
			   else
			   {
				   System.out.println("Invalid Input");
				   CreateUser();
			   }
			   query_is_a_users="INSERT INTO STUDENTS VALUES('"+username+"','"+ed_level+"')";
		}
		else if(input==2)
		{
			   role="Instructor";
			   query_is_a_users="INSERT INTO INSTRUCTORS VALUES('"+username+"')";
		}
		else if(input==3)
		{
			   role="Teaching Assistant";
			   ResultSet rs;
			   int no_of_courses=0;
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
				   CreateUser();
			   }
			   else{
				   System.out.println("Select the course for which the user will be the Teaching Assistant for");
				   int input1 = userInputScanner.nextInt();
				   if(input1<=no_of_courses){
					   System.out.println("Course chosen :"+ courses.get(input1-1)+ctokens.get(input1-1));
				   }
				   else{
					   System.out.println("Invalid Input");
					   	CreateUser();
				   }
			   
			   query_is_a_users="INSERT INTO TASSISTANTS VALUES ('"+username+"','"+courses.get(input1-1)+"','"+ctokens.get(input1-1)+"')";
			   }
		}
		else{
			System.out.println("Invalid Input");
			CreateUser();
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
    			String query_users="INSERT INTO USERS VALUES('"+username+"','"+fname+"','"+lname+"','"+password+"')";
    			String query_users_roles="INSERT INTO USERS_ROLES VALUES('"+username+"','"+role+"')";
    			stmt = conn.createStatement();
    			stmt.executeUpdate(query_users);
    			stmt.executeUpdate(query_users_roles);
    			stmt.executeUpdate(query_is_a_users);
    			}finally {close(conn);
    			close(stmt);
    			close(rs);
    			}
    	}catch(Throwable oops) {
    	            oops.printStackTrace();
    	            }
    			System.out.print("User Account created successfully\n");
    			FirstPage();
	   	
 }
   
/*   public static void Validate(){
	   	System.out.print("\nEnter Username\n");
	    String name = userInputScanner.next();
	    
	    System.out.print("Enter Password\n");
	    String password = userInputScanner.next();
	   	
  }*/
}