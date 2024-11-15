package org.justgroup_;

import java.sql.Time;
import java.util.ArrayList;

public class QueueForLab {
    private String className;
    private Time timeOfQueueCreation;
    private Time timeOfClass;
    private ArrayList<Student> listOfStudent;

    public QueueForLab(String className, Time timeOfQueueCreation, Time timeOfClass) {
        this.className = className;
        this.timeOfQueueCreation = timeOfQueueCreation;
        this.timeOfClass = timeOfClass;
        listOfStudent = new ArrayList<>();
    }

    public String getClassName() {
        return className;
    }
    public void setClassName(String className) {
        this.className = className;
    }
    public String getTimeOfQueueCreation() {
        return timeOfQueueCreation.toString();
    }
    public String getTimeOfClass() {
        return timeOfClass.toString();
    }
    public ArrayList<Student> getListOfStudent() {
        return listOfStudent;
    }

    public void addStudentInQueue(Student student) {
        listOfStudent.add(student);
    }

    public void deleteStudentFromQueue(Student student) {
        listOfStudent.remove(student);
    }

    public String getListAsList(){
        int index = 1;
        String list = "\n";
        for(Student student : listOfStudent){
            list += index + ". " + student.getName() + ";\n";
        }
        return list;
    }
}
