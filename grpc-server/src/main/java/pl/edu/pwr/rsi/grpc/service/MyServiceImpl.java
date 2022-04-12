package pl.edu.pwr.rsi.grpc.service;

import pl.edu.pwr.rsi.grpc.interfaces.lib.*;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.ArrayList;
import java.util.Objects;

// server implementation - extends base class generated based on proto file
@GrpcService
public class MyServiceImpl extends MyServiceGrpc.MyServiceImplBase {
    ArrayList<Student> students = new ArrayList<>();
    byte[] schoolImage;

    // test method
    // grpcurl --plaintext -d "{\"name\": \"Klaudia\"}" localhost:9000 pl.edu.pwr.rsi.grpc.interface.MyService/SayHello
    @Override
    public void sayHello(SimpleRequest request, StreamObserver<SimpleReply> responseObserver) {
        SimpleReply reply = SimpleReply.newBuilder().setMessage("Hello " + request.getName()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    // implementation of method to add student on server - synchronous request
    // grpcurl --plaintext -d "{\"name\": \"Klaudia\", \"surname\": \"Błażyczek\", \"indexNumber\": 123123, \"fieldOfStudy\": \"Applied Computer Science\"}" localhost:9000 pl.edu.pwr.rsi.grpc.interface.MyService/AddStudent
    // grpcurl --plaintext -d "{\"name\": \"Anna\", \"surname\": \"Kowalska\", \"indexNumber\": 111111, \"fieldOfStudy\": \"Biology\"}" localhost:9000 pl.edu.pwr.rsi.grpc.interface.MyService/AddStudent
    @Override
    public void addStudent(pl.edu.pwr.rsi.grpc.interfaces.lib.Student request, StreamObserver<SimpleReply> responseObserver) {
        boolean ifFound = false;
        SimpleReply reply;

        for (Student student: students) {
            if (student.getIndexNumber() == request.getIndexNumber()) {
                ifFound = true;
                break;
            }
        }

        if (ifFound) {
            reply = SimpleReply.newBuilder().setMessage("Can't add student, because this index number is currently used").build();
        } else {
            Student student = new Student(request.getName(), request.getSurname(), request.getIndexNumber(), request.getFieldOfStudy());
            students.add(student);
            reply = SimpleReply.newBuilder().setMessage("Added - " + student.toString()).build();
        }

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    // implementation of method to get student by index number - synchronous request
    // grpcurl --plaintext -d "{\"indexNumber\": 123123}" localhost:9000 pl.edu.pwr.rsi.grpc.interface.MyService/GetStudent
    @Override
    public void getStudent(StudentIndex request, StreamObserver<pl.edu.pwr.rsi.grpc.interfaces.lib.Student> responseObserver) {
        Student foundStudent = null;
        pl.edu.pwr.rsi.grpc.interfaces.lib.Student reply;

        for (Student student: students) {
            if (student.getIndexNumber() == request.getIndexNumber()) {
                foundStudent = student;
            }
        }

        if (foundStudent != null) {
            reply = pl.edu.pwr.rsi.grpc.interfaces.lib.Student.newBuilder().setName(foundStudent.getName()).setSurname(foundStudent.getSurname()).setIndexNumber(foundStudent.getIndexNumber()).setFieldOfStudy(foundStudent.getFieldOfStudy()).build();
        } else {
            reply = pl.edu.pwr.rsi.grpc.interfaces.lib.Student.newBuilder().setName("Unknown").setSurname("Unknown").setIndexNumber(-1).setFieldOfStudy("Unknown").build();
        }

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    // method to add students on server - stream request
    // grpcurl --plaintext -d @ localhost:9000 pl.edu.pwr.rsi.grpc.interface.MyService/StreamAddStudents
    // {"name": "Marian", "surname": "Nowak", "indexNumber": 667788, "fieldOfStudy": "Architecture"}
    // {"name": "John", "surname": "Doe", "indexNumber": 987654, "fieldOfStudy": "Applied Computer Science"}
    @Override
    public StreamObserver<pl.edu.pwr.rsi.grpc.interfaces.lib.Student> streamAddStudents(StreamObserver<SimpleReply> responseObserver) {
        return new StreamObserver<pl.edu.pwr.rsi.grpc.interfaces.lib.Student>() {
            boolean ifAnyFound = false;

            @Override
            public void onNext(pl.edu.pwr.rsi.grpc.interfaces.lib.Student studentToAdd) {
                boolean ifFound = false;

                for (Student student: students) {
                    if (student.getIndexNumber() == studentToAdd.getIndexNumber()) {
                        ifAnyFound = true;
                        ifFound = true;
                        break;
                    }
                }

                if(!ifFound) {
                    students.add(new Student(studentToAdd.getName(), studentToAdd.getSurname(), studentToAdd.getIndexNumber(), studentToAdd.getFieldOfStudy()));
                }
            }

            @Override
            public void onError(Throwable throwable) {
                responseObserver.onNext(SimpleReply.newBuilder().setMessage("Data upload failed: " + throwable.getCause()).build());
                responseObserver.onCompleted();
            }

            @Override
            public void onCompleted() {
                if (!ifAnyFound) {
                    responseObserver.onNext(SimpleReply.newBuilder().setMessage("Data upload completed").build());
                    responseObserver.onCompleted();
                } else {
                    responseObserver.onNext(SimpleReply.newBuilder().setMessage("Data upload completed partially due to indexes repetitions").build());
                    responseObserver.onCompleted();
                }
            }
        };
    }

    // method to read students from server - stream request
    // Thread.sleep(3000) was added to show progress bar on client side
    // grpcurl --plaintext localhost:9000 pl.edu.pwr.rsi.grpc.interface.MyService/StreamGetStudents
    @Override
    public void streamGetStudents(Empty request, StreamObserver<pl.edu.pwr.rsi.grpc.interfaces.lib.Student> responseObserver) {
        for (Student student: students) {
            pl.edu.pwr.rsi.grpc.interfaces.lib.Student reply = pl.edu.pwr.rsi.grpc.interfaces.lib.Student.newBuilder().setName(student.getName()).setSurname(student.getSurname()).setIndexNumber(student.getIndexNumber()).setFieldOfStudy(student.getFieldOfStudy()).build();
            responseObserver.onNext(reply);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        responseObserver.onCompleted();
    }

    // method to get amount of students by field of study - asynchronous request
    // grpcurl --plaintext -d "{\"fieldOfStudy\": \"Applied Computer Science\"}" localhost:9000 pl.edu.pwr.rsi.grpc.interface.MyService/AmountOfStudentsByFieldOfStudy
    @Override
    public void amountOfStudentsByFieldOfStudy(FieldOfStudy request, StreamObserver<Amount> responseObserver) {
        Thread other = new Thread(() -> {
            int counter = 0;

            for (Student student: students) {
                if(Objects.equals(student.getFieldOfStudy(), request.getFieldOfStudy())) {
                    counter++;
                }
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Amount amount = Amount.newBuilder().setAmount(counter).build();
            responseObserver.onNext(amount);
            responseObserver.onCompleted();
        });
        other.start();
    }

    // helper method to inform client about data size
    @Override
    public void recordsCount(Empty request, StreamObserver<CountReply> responseObserver) {
        CountReply reply = CountReply.newBuilder().setCount(students.size()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    // method to upload image on server
    @Override
    public void uploadImage(UploadImageRequest request, StreamObserver<SimpleReply> responseObserver) {
        schoolImage = request.getImage().toByteArray();
        SimpleReply reply = SimpleReply.newBuilder().setMessage("Image added to server").build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    // method to read image from server
    @Override
    public void getImage(Empty request, StreamObserver<ReadImageReply> responseObserver) {
        ReadImageReply reply;
        if (schoolImage == null)
            reply = ReadImageReply.newBuilder().setMessage("No image").build();
        else
            reply = ReadImageReply.newBuilder().setMessage("Image downloaded").setImage(ByteString.copyFrom(schoolImage)).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
