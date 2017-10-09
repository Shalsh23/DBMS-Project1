import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.sql.*;

public class StudentHomePage{

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
	        FirstPage();       
	    }*/

	public static void FirstPage(){
		System.out.print("\n1. Login \n2. Create User\n3. Exit\n");
		int input = userInputScanner.nextInt();
		if(input==1){
			login();
		}
		else if(input==2){

		}
		else if(input==3){
			return;
		}
		else{
			System.out.println("\nInvalid Input");
			FirstPage();
		}

	}
	public static void login(){
		System.out.println("\n1. Enter UId");
		String uid=userInputScanner.next();
		System.out.println("\n1. Enter Password");
		String password=userInputScanner.next();
		//login(uid,password);
		StudentMain();
	}
	public static void StudentMain(){
		System.out.print("\n1. Select Course \n2. Add Course\n3. Back\n");
		int input = userInputScanner.nextInt();
		if(input==1){
			selectcourse();
		}
		else if(input==2){
			addcourse();
		}
		else if(input==3){
			System.out.println("Logging out");
			LoginPage.delete_current_user();
			System.exit(0);
		}
		else{
			System.out.println("\nInvalid Input");
			StudentMain();
		}

	}
	public static void selectcourse(){
		//String[] courselist={"DBMS","Junk Course"};
		//for(int i=0;i<courselist.length;i++){
		//System.out.println(i+" ."+courselist);
		//}
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
				System.out.println(no_of_courses+".     "+s + "   " + n+'\t'+token);
				ctokens.add(token);
				courses.add(s);

			}
		} catch(Throwable oops) {
			oops.printStackTrace();
		}
		int back_option=no_of_courses+1;
		System.out.println(back_option+ ". Enter "+back_option+" to Go Back");
		int input = userInputScanner.nextInt();

		if(input==back_option){
			StudentMain();
		}

		else if(input>back_option)
		{
			System.out.println("Invalid Input");
			System.out.println("Going back to the previous menu");
			StudentMain();
		}
		else{
			System.out.println("Course chosen :"+ courses.get(input-1));
			courseoptions(courses.get(input-1),ctokens.get(input-1));
		}
	}
	public static void addcourse(){
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


		System.out.println("Enter course token\n");
		String token=sc.nextLine();
		//Check the token if it exists first
		String query="Select ctoken from Course_Offerings WHERE ctoken='"+token+"'";
		ResultSet res;
		res=jdbcExecute(query);
		int exists=0;
		try{
			while(res.next())
			{
				String s = res.getString("ctoken");
				//System.out.println(s);
				if(s.equals(token))
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
			System.out.println("Invalid ID.Course does not exist");
			StudentMain();
		}
		else{
			//Check the due date
			String date_check="Select case when to_char(sysdate,'YYYYMMDD') > to_char(co.start_dt,'YYYYMMDD') then -1 else 0 end reg_status from Course_Offerings co WHERE co.ctoken='"+token+"'";
			ResultSet res2;
			int due_date=0;
			res2=jdbcExecute(date_check);
			try{
				while(res2.next())
				{
					int s = res2.getInt("reg_status");
					if(s==-1)
					{
						due_date=1;
						break;
					}
				}
			}catch(Throwable oops) {
				oops.printStackTrace();
			}//end catch
			if(due_date==1)
			{
				System.out.println("Course start date over. Cannot Register");
				StudentMain();
			}
			else
			{
				//Check max enrollment
				String enrollment="Select case when co.maximum_enrollment > co.current_enrollment then 1 else 0 end as enr from course_offerings co where co.ctoken='"+token+"'";
				ResultSet res3;
				res3=jdbcExecute(enrollment);
				int enr_failure=0;
				try{
					while(res3.next())
					{
						int s = res2.getInt("enr");
						if(s==0)
						{
							enr_failure=1;
							break;
						}
					}
				}catch(Throwable oops) {
					oops.printStackTrace();
				}

				if(enr_failure==1)
				{
					System.out.println("Maximum enrollment reached. Cannot enroll");
					StudentMain();
				}
				else
				{
					//Insert statement here
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
							String query_cid="Select cid from Course_Offerings WHERE ctoken='"+token+"'";
							rs11=jdbcExecute(query_cid);
							String cid=rs11.getString("cid");
							stmt.executeUpdate("INSERT INTO COURSE_STUDENTS VALUES('"+unityid+"','"+cid+"','"+token+"')");
						}finally {}
					}catch(Throwable oops) {
						oops.printStackTrace();
					}
					System.out.println("Course added successfully");
					StudentMain();
				}
			}
		}
	}
	public static void courseoptions(String course, String ctoken){
		//To be displayed only if the student is enrolled
		int student_enrolled=0;

		//Get the current user
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
		String query2="Select unityid from Course_Students where unityid='"+unityid+"' and cid='"+course+"' and ctoken='"+ctoken+"'";
		result=jdbcExecute(query2);
		try{
			while (result.next()) {
				userid=result.getString("unityid");
				if(userid.equals(unityid) && userid!="")
					student_enrolled=1;
			}
		} catch(Throwable oops) {
			oops.printStackTrace();
		}

		if(student_enrolled==0)
		{
			System.out.println("Permission denied as you are not enrolled in this course");
			StudentMain();
		}
		else
		{
			String temp=course;
			System.out.print("\n1. View Scores \n2. Attempt Homework\n3. View Past Submissions\n4. View Notifications\n5. Back\n");
			int input = userInputScanner.nextInt();

			if(input==1){
				viewscores(course,ctoken);
				courseoptions(course,ctoken);

			}
			else if(input==2){
				attempthomework(course,ctoken);
				courseoptions(course,ctoken);
			}
			else if(input==3){
				viewsubmissions(course,ctoken);
				courseoptions(course,ctoken);
			}
			else if(input==4){
				displayNotification(course,ctoken);
				courseoptions(course,ctoken);
			}
			else if(input==5){
				selectcourse();
			}
			else{
				System.out.println("Invalid Input");
				courseoptions(temp,"dummy");
			}
		}
	}

	public static void viewscores(String course,String ctoken){
		String query = "SELECT Submission.Score, Submission.HWID, Submission.TimeStamp FROM Submission, Homework WHERE Submission.UnityID IN (SELECT UNITYID FROM CURRENT_USER) AND Submission.HWID=Homework.HWID AND CID='"+course +"'AND CTOKEN='"+ctoken+"'";
		ResultSet myScores = jdbcExecute(query); //Table containing homework scores

		//Displaying the table of hw scores
		try{
			ResultSetMetaData rsmd = myScores.getMetaData();
			int columnsNumber = rsmd.getColumnCount();

			while(myScores.next()){
				for(int i = 1; i <= columnsNumber; i++){
					if(i>1) System.out.print(", ");
					String columnValue = myScores.getString(i);
					System.out.print(columnValue+" "+rsmd.getColumnName(i));
				}
				System.out.println("");
			}

		} catch(Throwable oops) {
			oops.printStackTrace();
		}
		System.out.println("");	    	 
		courseoptions(course,ctoken);
		//courseoptions(course,"dummy");
	}
	public static void attempthomework(String course,String ctoken){

		int no_of_hws=0;
		int score=0;
		String submission_report="";
		int correctpoints=0;
		int incorrectpoints=0;
		String query="";
		ArrayList<String> hws=new ArrayList<String>();
		String query1="Select hwid from homework where cid='"+course+"' and ctoken='"+ctoken+"' order by hwid";
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
			courseoptions(course,ctoken);
		}
		else
		{
			int back_option=no_of_hws+1;
			System.out.println(back_option+"\tEnter "+back_option+" to Go Back");
			System.out.println("Select a Homework:\n");
			int selection = userInputScanner.nextInt();
			if(selection <=no_of_hws)
			{
				int selection_hw=Integer.parseInt(hws.get(selection -1));
				//Check for the due date of hw
				String date_check="Select case when to_char(sysdate,'YYYYMMDD') > to_char(enddate,'YYYYMMDD') then -1 else 0 end hw_status from Homework WHERE hwid="+selection_hw;
				ResultSet res2;
				int due_date=0;
				int attempt_number=0;
				res2=jdbcExecute(date_check);
				try{
					while(res2.next())
					{
						int s = res2.getInt("hw_status");
						if(s==-1)
						{
							due_date=1;
							break;
						}
					}
				}catch(Throwable oops) {
					oops.printStackTrace();
				}//end catch
				if(due_date==1)
				{
					System.out.println("Homework deadline has already passed");
					attempthomework(course,ctoken);
				}

				else
				{
					//Get User ID
					String user_id="Select unityid from current_user";
					String unityid="";
					ResultSet rs22=jdbcExecute(user_id);
					try{
						while(rs22.next())
						{
							unityid=rs22.getString("unityid");
						}
					}catch(Throwable oops) {
						oops.printStackTrace();
					}


					String attempts="Select COUNT(*) as attempts FROM SUBMISSION WHERE unityid='"+unityid+"' and hwid="+selection_hw+" group by hwid, unityid";
					rs22 = jdbcExecute(attempts);
					try{
						while(rs22.next())
						{
							attempt_number=rs22.getInt("attempts");
						}
					}catch(Throwable oops) {
						oops.printStackTrace();
					}
					attempt_number++;
					int allowed=0;
					String allowed_a="Select numofretries from homework where hwid="+selection_hw;
					rs22 = jdbcExecute(allowed_a);
					try{
						while(rs22.next())
						{
							allowed=rs22.getInt("numofretries");
						}
					}catch(Throwable oops) {
						oops.printStackTrace();
					}
					if(attempt_number > allowed)
					{
						System.out.println("Maximum attempts for this homework reached");
						attempthomework(course,ctoken);

					}
					else
					{
						//System.out.println("Selected Hw : "+selection_hw+" and not "+ selection);
						String numquestions="select numofquestions from homework where hwid="+selection_hw;
						ResultSet result = jdbcExecute(numquestions);
						int nq=0;
						try{
							while (result.next()) {
								nq=result.getInt("numofquestions");

							}
						} catch(Throwable oops) {
							oops.printStackTrace();
						}
						//Check if the question bank has enough number of questions to display the homework
						int question_bank=0;
						numquestions="Select COUNT(*) as ques_bank from hw_questions where hwid="+selection_hw+" group by hwid";
						result=jdbcExecute(numquestions);
						try{
							while (result.next()) {
								question_bank=result.getInt("ques_bank");
							}
						} catch(Throwable oops) {
							oops.printStackTrace();
						}

						//The actual check
						if(question_bank < nq)
						{	
							System.out.println("This Homework not yet ready. Going back to the list of available Homeworks");
							attempthomework(course,ctoken);
						}

						else
						{
							//Get correct points, incorrect points for this hw
							query="Select pointsforcorrectques from homework where hwid="+selection_hw;
							result=jdbcExecute(query);
							try{
								while (result.next()) {
									correctpoints=result.getInt("pointsforcorrectques");

								}
							} catch(Throwable oops) {
								oops.printStackTrace();
							}

							//Get incorrect points
							query="Select pointsforincorrectques from homework where hwid="+selection_hw;
							result=jdbcExecute(query);
							try{
								while (result.next()) {
									incorrectpoints=result.getInt("pointsforincorrectques");

								}
							} catch(Throwable oops) {
								oops.printStackTrace();
							}

							getQuesIDs(nq,selection_hw,score,correctpoints,incorrectpoints,submission_report);

						}//end of "Enough questions available" check
					}//end of "attempts" check
				}// end of "Deadline check"
			}// end of "user input" check
			else if(selection==back_option)
			{
				courseoptions(course,ctoken);
			}
			else
			{
				System.out.println("Invalid Input");
				attempthomework(course,ctoken);
			}
		}

	}

	private static void getQuesIDs(int nq,int selection,int score, int correctpoints, int incorrectpoints, String s) {
		String query1="Select qid from hw_questions where hwid="+selection;
		String long_report="";
		String long_desc="";
		String corr_option_text="";
		int corr_option_id=0;
		int[] incorrect_options_id=new int[3];
		String desc;
		int j;
		ResultSet rs=jdbcExecute(query1);
		try{
			while (rs.next()) {
				insertintotemp(rs.getInt("qid"));

			}
		} catch(Throwable oops) {
			oops.printStackTrace();
		}
		ArrayList<Integer> qids =new ArrayList<Integer>();
		//String rand="select * from temp order by qid limit n in (select rand()) offset 0";
		String rand="select * from ( select * from temp order by dbms_random.value) a where rownum<=1";
		while(qids.size()<nq){
			ResultSet rs1=jdbcExecute(rand);
			try{
				while (rs1.next()) {
					qids.add(rs1.getInt("qid"));
				}
			} catch(Throwable oops) {
				oops.printStackTrace();
			}
		}
		System.out.println("Homework starts...");
		System.out.println("");
		//Integer[] qarr=(Integer[]) qids.toArray();
		for(int i=0;i<nq;i++){
			desc="";
			corr_option_id=0;
			int k=0;
			long_desc="";
			corr_option_text="";
			for(k=0;k<2;k++)
				incorrect_options_id[k]=0;


			//s=displayques(qids.get(i),score,correctpoints,incorrectpoints,s);
			String query11="select q.ques from questions q, hw_questions hq where hq.qid=q.qid and q.qid="+qids.get(i)+" group by q.ques";
			String correctoption="select * from( select o.oid, o.optiontext from correctoptions co, options o where co.oid=o.oid and o.qid="+qids.get(i)+" order by dbms_random.value ) where rownum<=1";
			String incorrectoptions="select * from(select o.oid, o.optiontext from incorrectoptions ico, options o where ico.oid=o.oid and o.qid="+qids.get(i)+" order by dbms_random.value) where rownum<=3";
			rs=jdbcExecute(query11);
			int op_no=0;
			try{
				while (rs.next()) {
					System.out.println(rs.getString("ques"));
					s=s+rs.getString("ques")+'\n';
					long_report=long_report+rs.getString("ques")+'\n';
					ResultSet rs11=jdbcExecute(correctoption);
					try{
						while (rs11.next()) {
							op_no++;
							corr_option_id=rs11.getInt("oid");
							corr_option_text=rs11.getString("optiontext");
							System.out.println(Integer.toString(op_no)+'\t'+rs11.getString("optiontext"));
							s=s+Integer.toString(op_no)+'\t'+rs11.getString("optiontext")+'\n';
							long_report=long_report+Integer.toString(op_no)+'\t'+rs11.getString("optiontext")+'\n';
						}
					} catch(Throwable oops) {
						oops.printStackTrace();
					}

					rs11=jdbcExecute(incorrectoptions);
					j=0;
					try{
						while (rs11.next()) {
							op_no++;
							incorrect_options_id[j]=rs11.getInt("oid");
							j++;
							System.out.println(Integer.toString(op_no)+'\t'+rs11.getString("optiontext"));
							s=s+Integer.toString(op_no)+'\t'+rs11.getString("optiontext")+'\n';
							long_report=long_report+Integer.toString(op_no)+'\t'+rs11.getString("optiontext")+'\n';
						}
					} catch(Throwable oops) {
						oops.printStackTrace();
					}
					//displayoptions(correctoption);
					//displayoptions(incorrectoptions);
				}
			} catch(Throwable oops) {
				oops.printStackTrace();
			}

			int input = userInputScanner.nextInt();
			if(input==1)
			{
				//Update score
				score=score+correctpoints;
				s=s+"Your choice : 1"+'\n';
				s=s+"Correct Answer : Yes"+'\n';
				s=s+"Points for this question: "+correctpoints+'\n';

				long_report=long_report+"Your choice : 1"+'\n';
				long_report=long_report+"Correct Answer : Yes"+'\n';
				long_report=long_report+"Points for this question: "+correctpoints+'\n';

				//Getting the short desc for this option
				String shrt_desc="Select shrtdesc from options where oid="+corr_option_id;
				ResultSet rs123=jdbcExecute(shrt_desc);
				try{
					while (rs123.next()) {
						desc=rs123.getString("shrtdesc");
					}
				} catch(Throwable oops) {
					oops.printStackTrace();
				}


			}
			else if(input==2 || input==3 || input==4)
			{
				//Update score
				score=score+incorrectpoints;
				s=s+"Your choice : "+input+'\n';
				s=s+"Correct Answer : No"+'\n';
				s=s+"Points for this question: "+incorrectpoints+'\n';

				long_report=long_report+"Your choice : "+input+'\n';
				long_report=long_report+"Correct Answer : No"+'\n';
				long_report=long_report+"Points for this question: "+incorrectpoints+'\n';


				String shrt_desc="Select shrtdesc from options where oid="+incorrect_options_id[input-2];
				ResultSet rs123=jdbcExecute(shrt_desc);
				try{
					while (rs123.next()) {
						desc=rs123.getString("shrtdesc");
					}
				} catch(Throwable oops) {
					oops.printStackTrace();
				}
			}

			else
			{
				System.out.println("Invalid choice selected");
				score=score+incorrectpoints;
				s=s+"Your choice : "+input+'\n';
				s=s+"Correct Answer : No"+'\n';
				s=s+"Points for this question: "+incorrectpoints+'\n';

				long_report=long_report+"Your choice : "+input+'\n';
				long_report=long_report+"Correct Answer : No"+'\n';
				long_report=long_report+"Points for this question: "+incorrectpoints+'\n';

				desc="No Description available for this as invalid choice was selected";
				long_desc="No Description available for this as invalid choice was selected";

			}

			s=s+"--------Description for your choice --------:\n"+desc+'\n'+'\n';
			//Question ID : qids.get(i)
			String l_query="Select longdesc from correctoptions where qid="+qids.get(i)+" and oid="+corr_option_id;
			ResultSet resw=jdbcExecute(l_query);
			try{
				while (resw.next()) {
					long_desc=resw.getString("longdesc");
				}
			} catch(Throwable oops) {
				oops.printStackTrace();
			}

			long_report=long_report+"--------Description for the correct choice--------:\nCorrect Choice: "+corr_option_text+'\n'+'\n'+long_desc+'\n'+'\n'; 	
		}
		//System.out.println(s);
		//System.out.println(score);	    	
		//Quiz ended
		System.out.println("Thank you for taking the quiz");
		System.out.println("Your Score "+score);
		s=s+'\n'+'\n'+"Your Score "+score+'\n';
		long_report=long_report+'\n'+'\n'+"Your Score "+score+'\n';

		int submission_id=0;
		try{
			String user = "aboke";	
			String passwd = "200061305";
			Connection conn = null;
			Statement stmt = null;
			ResultSet rs22 = null;
			String no_of_sbmissions_yet="Select COUNT(*) as sb_count from submission";
			rs22=jdbcExecute(no_of_sbmissions_yet);
			try{
				while(rs22.next())
				{
					submission_id=rs22.getInt("sb_count");
				}
			}catch(Throwable oops) {
				oops.printStackTrace();
			}
			submission_id=submission_id+1;

			//Get the current user
			String user_id="Select unityid from current_user";
			String unityid="";
			rs22=jdbcExecute(user_id);
			try{
				while(rs22.next())
				{
					unityid=rs22.getString("unityid");
				}
			}catch(Throwable oops) {
				oops.printStackTrace();
			}

			//Got the submission id, userid
			//insert into submission values(current_timestamp,1,1,'test','aneela',1);
			String insert_submission="INSERT INTO SUBMISSION VALUES(current_timestamp,"+submission_id+","+score+",'"+s+"','"+unityid+"',"+selection+",'"+long_report+"')";
			String del_temp="Delete from temp";
			//System.out.println(insert_submission);
			try{
				conn = DriverManager.getConnection(jdbcURL, user, passwd);
				stmt = conn.createStatement();
				stmt.executeUpdate(insert_submission);
				stmt.executeUpdate(del_temp);
			}finally{close(conn);
			close(stmt);
			}

		}catch(Throwable oops) {
			oops.printStackTrace();
		}
		System.out.println("HW Submitted Successfully");
	}
	/*private static String displayques(int qid,int score, int correctpoints, int incorrectpoints, String s){
			//String query1="Select ques from hw_questions where qid="+qid;
			String query1="select q.ques from questions q, hw_questions hq where hq.qid=q.qid and q.qid="+qid+" group by q.ques";
			String correctoption="select * from( select o.optiontext from correctoptions co, options o where co.oid=o.oid and o.qid="+qid+" order by dbms_random.value ) where rownum<=1";
			String incorrectoptions="select * from(select o.optiontext from incorrectoptions ico, options o where ico.oid=o.oid and o.qid="+qid+" order by dbms_random.value) where rownum<=3";
	    	ResultSet rs=jdbcExecute(query1);
	    	int op_no=0;
	    	try{
	    		while (rs.next()) {
	    			System.out.println(rs.getString("ques"));
	    			s=s+rs.getString("ques")+'\n';
	    	    	ResultSet rs11=jdbcExecute(correctoption);
	    	    	try{
	    	    		while (rs11.next()) {
	    	    			op_no++;
	    	    			System.out.println(Integer.toString(op_no)+'\t'+rs11.getString("optiontext"));
	    	    			s=s+Integer.toString(op_no)+'\t'+rs11.getString("optiontext")+'\n';
	    	    		}
	    	    	} catch(Throwable oops) {
	    	    		oops.printStackTrace();
	    	    	}

	    	    	rs11=jdbcExecute(incorrectoptions);
	    	    	try{
	    	    		while (rs11.next()) {
	    	    			op_no++;
	    	    			System.out.println(Integer.toString(op_no)+'\t'+rs11.getString("optiontext"));
	    	    			s=s+Integer.toString(op_no)+'\t'+rs11.getString("optiontext")+'\n';
	    	    		}
	    	    	} catch(Throwable oops) {
	    	    		oops.printStackTrace();
	    	    	}
	    			//displayoptions(correctoption);
	    			//displayoptions(incorrectoptions);
	    		}
	    	} catch(Throwable oops) {
	    		oops.printStackTrace();
	    	}

	    	int input = userInputScanner.nextInt();
	    	if(input==1)
	    	{
	    		//Update score
	    		s=s+"Your choice : 1"+'\n';
	    		s=s+"Correct Answer : Yes"+'\n';
	    	}
	    	else
	    	{
	    		//Update score
	    		s=s+"Your choice : "+input+'\n';
	    		s=s+"Correct Answer : No"+'\n';
	    	}
	    	return s;

		}
		private static void displayoptions(String query1){
	    	ResultSet rs=jdbcExecute(query1);
	    	try{
	    		while (rs.next()) {
	    			System.out.println(rs.getString("optiontext"));
	    		}
	    	} catch(Throwable oops) {
	    		oops.printStackTrace();
	    	}
		}*/
	private static void insertintotemp(int qid) {
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
				stmt.executeUpdate("INSERT INTO temp VALUES("+qid+")");
			}finally {}
		}catch(Throwable oops) {
			oops.printStackTrace();
		}
	}

	public static void displayNotification(String course,String ctoken){

		String query = "SELECT Homework.HWID FROM Homework WHERE CID='"+course+"' AND CTOKEN='"+ctoken+"' AND (SELECT trunc(sysdate - Homework.EndDate) FROM dual)=0";
		ResultSet dueHW = jdbcExecute(query); //Table containing Homeworks due within a day

		//Accessing the table of due HW
		try{
			ResultSetMetaData rsmd = dueHW.getMetaData();
			int columnsNumber = rsmd.getColumnCount();

			while(dueHW.next()){
				for(int i = 1; i <= columnsNumber; i++){

					String columnvalue = dueHW.getString(i);
					String query1 = "SELECT * FROM Submission WHERE HWID='"+columnvalue+"' AND Submission.UnityID IN (SELECT UnityID FROM Current_User)";
					ResultSet submittedHW = jdbcExecute(query1);
					if(!submittedHW.next())
						System.out.println("You have pending HW due within a day!");
				}
				System.out.println("");
			}

		} catch(Throwable oops) {
			oops.printStackTrace();
		}


		courseoptions(course,ctoken);
	}
	public static void viewsubmissions(String cid, String ctoken ){
		ArrayList<Integer> hw_past_due=new ArrayList<Integer>();
		ArrayList<Integer> hw_due=new ArrayList<Integer>();
		ArrayList<Integer> attempt_past_due=new ArrayList<Integer>();
		ArrayList<Integer> attempt_due=new ArrayList<Integer>();
		int hw_past=0;
		int hw_due_count=0;

		//Get the user id;
		String userid="";
		String query2="Select unityid from current_user";
		ResultSet result=jdbcExecute(query2);
		try{
			while (result.next()) {
				userid=result.getString("unityid");
			}
		} catch(Throwable oops) {
			oops.printStackTrace();
		}

		String query_past="select s.hwid, count(*) as no_of_attempts from submission s, homework h where s.hwid=h.hwid and h.cid='"+cid+"' and h.ctoken='"+ctoken+"' and s.unityid='"+userid+"' and sysdate > h.enddate group by s.hwid order by s.hwid";
		ResultSet rs=jdbcExecute(query_past);
		int i=1;
		int records=0;
		int past_records=0;
		System.out.println("Homeworks past due date");
		try{
			while (rs.next()) {
				int hw_id = rs.getInt("hwid");
				int attempts=rs.getInt("no_of_attempts");
				for(i=1;i<=attempts;i++)
				{
					hw_past_due.add(hw_id);
					attempt_past_due.add(i);
					records++;
					past_records++;
					System.out.println(Integer.toString(records) + ".    HW "+Integer.toString(hw_id)+ '\t'+ "Attempt "+ Integer.toString(i));
				}
			}
		} catch(Throwable oops) {
			oops.printStackTrace();
		}

		System.out.println("Homeworks within due date");
		String query_within="select s.hwid, count(*) as no_of_attempts from submission s, homework h where s.hwid=h.hwid and h.cid='"+cid+"' and h.ctoken='"+ctoken+"' and s.unityid='"+userid+"' and sysdate <= h.enddate group by s.hwid order by s.hwid";
		rs=jdbcExecute(query_within);
		try{
			while (rs.next()) {
				int hw_id = rs.getInt("hwid");
				int attempts=rs.getInt("no_of_attempts");
				for(i=1;i<=attempts;i++)
				{
					hw_due.add(hw_id);
					attempt_due.add(i);
					records++;
					System.out.println(Integer.toString(records) + ".    HW "+Integer.toString(hw_id)+ '\t'+ "Attempt "+ Integer.toString(i));
				}
			}
		} catch(Throwable oops) {
			oops.printStackTrace();
		}
		//System.out.println("Enter 0 to Go Back");
		if(records==0)
		{
			System.out.println("No Submissions to display");
			return;
		}
		else
		{
			int hw_id_select=0;
			int attempt_select=0;
			String final_query="";
			int selection=userInputScanner.nextInt();
			if(selection >0 && selection<=past_records)
			{
				System.out.println("HW Chosen : "+hw_past_due.get(selection-1)+"  Attempt : "+attempt_past_due.get(selection-1));
				hw_id_select=hw_past_due.get(selection-1);
				attempt_select=attempt_past_due.get(selection-1);
				final_query="select report from (select sb.long_report as report, rank() over (partition by sb.unityid,sb.hwid order by sb.timestamp ) attempt  from submission sb where unityid='"+userid+"' and hwid="+hw_id_select+") a where a.attempt="+attempt_select;
				rs=jdbcExecute(final_query);
				System.out.println("Submission Details:");
				try{
					while (rs.next()) {
						System.out.println(rs.getString("long_report"));
					}
				} catch(Throwable oops) {
					oops.printStackTrace();
				}
			}

			else if(selection > past_records && selection <=records)
			{		    			
				System.out.println("HW Chosen : "+hw_due.get(selection-1)+"  Attempt : "+attempt_due.get(selection-1));
				hw_id_select=hw_due.get(selection-1);
				attempt_select=attempt_due.get(selection-1);
				System.out.println(attempt_select);
				final_query="select report from (select sb.report as report, rank() over (partition by sb.unityid,sb.hwid order by sb.timestamp ) attempt  from submission sb where unityid='"+userid+"' and hwid="+hw_id_select+") a where a.attempt="+attempt_select;
				//System.out.println(final_query);
				rs=jdbcExecute(final_query);
				System.out.println("Submission Details:");
				try{
					while (rs.next()) {
						System.out.println(rs.getString("report"));
					}
				} catch(Throwable oops) {
					oops.printStackTrace();
				}
			}
			else
			{
				System.out.println("Invalid Input");
				viewsubmissions(cid,ctoken);
			}	
		}
	}
}
