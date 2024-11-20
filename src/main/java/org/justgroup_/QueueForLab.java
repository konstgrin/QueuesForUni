package org.justgroup_;

import java.sql.Time;
import java.time.DayOfWeek;
import java.util.ArrayList;

public class QueueForLab {
    private String className;
    private Time timeOfQueueCreation;
    private DayOfWeek dayOfWeek;
    private Time timeOfClass;
    private ArrayList<Student> listOfStudent;
    private long idInQueue;

    public QueueForLab(String className, Time timeOfQueueCreation, DayOfWeek dayOfWeek, Time timeOfClass, long idInQueue) {
        this.className = className;
        this.timeOfQueueCreation = timeOfQueueCreation;
        this.dayOfWeek = dayOfWeek;
        this.timeOfClass = timeOfClass;
        this.idInQueue = idInQueue;
        listOfStudent = new ArrayList<>();
    }

    public String getClassName() {
        return className;
    }
    public void setClassName(String className) {
        this.className = className;
    }
    public Time getTimeOfQueueCreation() {
        return timeOfQueueCreation;
    }
    public DayOfWeek getDayOfWeek() { return dayOfWeek; }
    public Time getTimeOfClass() {
        return timeOfClass;
    }
    public long getIdInQueue() {
        return idInQueue;
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
            list += index++ + ". @" + student.getName() + ";\n";
        }
        return list;
    }

    public void setListOfStudent(ArrayList<Student> listOfStudent) {
        this.listOfStudent = listOfStudent;
    }
}
