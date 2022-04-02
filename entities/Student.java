package entities;

public class Student {
    
    //identify the student
    //id + the state the student is in
    public int studentID;
    public int studentState;

    public Student(int studentID){
        //initial state
        this.studentID = studentID;
        studentState = StudentStates.GGTRT;
    }

    public void setStudentID(int id){
        studentID = id;
    }

    public int getStudentID(){
        return studentID;
    }

    public void setStudentState(int state){
        studentState = state;
    }

    public int getStudentState(){
        return studentState;
    }

    //function run - thread
}
