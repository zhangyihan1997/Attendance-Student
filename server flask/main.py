import os

from flask import Flask, request, Response, send_from_directory
import cmath
from math import radians, sin, cos, asin, sqrt, atan2
from flask_sqlalchemy import SQLAlchemy

from sqlalchemy.dialects.mysql import DOUBLE
from flask_migrate import Migrate
import time
import json
import pymysql
import werkzeug
import base64
x_pi = 3.14159265358979324 * 3000.0 / 180.0


pymysql.install_as_MySQLdb()

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql://admin:1234567@47.102.105.203:3306/android'

db = SQLAlchemy(app)
migrate = Migrate(app, db)


# attendance database
class Attendance(db.Model):
    student_id = db.Column('student_id', db.Integer, primary_key=True)
    attendance = db.Column(db.SmallInteger, nullable=False)
    time = db.Column(db.TIMESTAMP, nullable=False)

    def __init__(self, student_id, attendance):
        self.student_id = student_id
        self.attendance = attendance


class TeacherCourse(db.Model):
    teacher_id = db.Column('teacher_id', db.Integer, primary_key=True)
    classroom = db.Column(db.String(20), nullable=False)
    coursetime = db.Column(db.String(20), nullable=False)

    def __init__(self, teacher_id, classroom, coursetime):
        self.teacher_id = teacher_id
        self.classroom = classroom
        self.coursetime = coursetime


class StudentCourse(db.Model):
    student_id = db.Column('student_id', db.Integer, primary_key=True)
    name = db.Column(db.String(20), nullable=False)
    classroom = db.Column(db.String(20), nullable=False)
    teacher = db.Column(db.String(20), nullable=False)
    time = db.Column(db.String(20), nullable=False)

    def __init__(self, student_id, name, classroom, teacher, time,):
        self.student_id = student_id
        self.name = name
        self.classroom = classroom
        self.teacher = teacher
        self.time = time


# photo database
class Photo(db.Model):
    student_id = db.Column('student_id', db.Integer, primary_key=True)
    pathname = db.Column(db.String(50), nullable=False)


# student database
class Student(db.Model):
    student_id = db.Column('student_id', db.Integer, primary_key=True)
    name = db.Column(db.String(20), nullable=False)
    password = db.Column(db.String(50), nullable=False)


# teacher database
class Teacher(db.Model):
    teacher_id = db.Column('teacher_id', db.Integer, primary_key=True)
    name = db.Column(db.String(20), nullable=False)
    password = db.Column(db.String(50), nullable=False)

    def __init__(self, teacher_id, name, password):
        self.teacher_id = teacher_id
        self.name = name
        self.password = password


# student location database
class StudentLocation(db.Model):
    __tablename__ = 'student_location'
    student_id = db.Column('student_id', db.Integer, primary_key=True)
    longitude = db.Column(DOUBLE, nullable=False)
    latitude = db.Column(DOUBLE, nullable=False)
    time = db.Column(db.TIMESTAMP, nullable=False)
    attendance = db.Column(db.SmallInteger, nullable=False)

    def __init__(self, longitude, latitude, student_id, time, attendance):
        self.longitude = longitude
        self.latitude = latitude
        self.student_id = student_id
        self.time = time
        self.attendance = attendance


# teacher location database
class TeacherLocation(db.Model):
    __tablename__ = "teacher_location"
    teacher_id = db.Column('teacher_id', db.Integer, primary_key=True)
    longitude = db.Column(DOUBLE, nullable=False)
    latitude = db.Column(DOUBLE, nullable=False)
    time = db.Column(db.TIMESTAMP, nullable=False)

    def __init__(self, longitude, latitude, teacher_id, time):
        self.longitude = longitude
        self.latitude = latitude
        self.teacher_id = teacher_id
        self.time = time


# change type
def text_to_dict(text):
    dictionary = {}
    for txt in text.split("&"):
        arr = txt.split("=")
        dictionary[arr[0]] = arr[1]

    return dictionary


# teacher log in
@app.route('/user/teacher', methods=['POST'])
def check_teacher():
    status = False
    print(request.data)
    request_data = text_to_dict(str(request.data, encoding='utf-8'))
    print(request_data)
    teacher_id = request_data['teacher_id']
    password = request_data['password']
    teacher = Teacher.query.filter_by(teacher_id=teacher_id).first()
    if teacher.password == password:
        status = True
    print({
        'teacher_id': teacher.teacher_id,
        'status': status
    })
    return Response(json.dumps({
        'teacher_id': teacher.teacher_id,
        'status': status
    }), mimetype='application/json')


@app.route('/upload/Image', methods=['POST'])
def handle_request():
    print(request)
    img = request.files.get('upload')
    filename = '/var/www/html/img/' + img.filename
    print("\nReceived image File name : " + img.filename)
    img.save(filename)
    return filename


# @app.route('/download/Image1', methods=['GET'])
# def download():
#     if request.method == "GET":
#         if os.path.isfile(os.path.join('/root/img/', "1.jpg")):
#             status = True
#             Uri = "http://47.102.105.203:8082/root/img/1.jpg"
#             return Response(json.dumps({
#                 'Uri': Uri,
#                 'status': status
#             }), mimetype='application/json')


# @app.route('/root/img/', methods=['GET'])
# def download1():
#     if request.method == "GET":
#         if os.path.isfile(os.path.join('/root/img/', "1630003010.jpg")):
#             return send_from_directory('/root/img/', "1630003010.jpg", as_attachment=True)
#         pass


# @app.route('/download/Image3', methods=['GET'])
# def download():
#     if request.method == "GET":
#         if os.path.isfile(os.path.join('/root/img/', "1630003020.jpg")):
#             return send_from_directory('/root/img/', "1630003020.jpg", as_attachment=True)
#         pass


# @app.route('/download/Image4', methods=['GET'])
# def download():
#     if request.method == "GET":
#         if os.path.isfile(os.path.join('/root/img/', "1630003064.jpg")):
#             return send_from_directory('/root/img/', "1630003064.jpg", as_attachment=True)
#         pass


# student log in
@app.route('/user/student', methods=['POST'])
def check_student():
    status = False
    request_data = text_to_dict(str(request.data, encoding='utf-8'))
    student_id = request_data['student_id']
    password = request_data['password']
    student = Student.query.filter_by(student_id=student_id).first()
    if student.password == password:
        status = True
    return Response(json.dumps({
        'student_id': student.student_id,
        'status': status
    }), mimetype='application/json')


# get & put student location
@app.route('/location/student', methods=['GET', 'PUT'])
def add_student_location():
    if request.method == 'PUT':
        request_data = text_to_dict(str(request.data, encoding='utf-8'))
        print(request_data)
        longitude = request_data['longitude']
        latitude = request_data['latitude']
        student_id = request_data['student_id']
        location_time = request_data['time']
        print(location_time, type(location_time))
        location_time = time.localtime()
        exist_data = StudentLocation.query.filter_by(student_id=student_id).first()
        exist_data1 = Attendance.query.filter_by(student_id=student_id).first()
        if exist_data:
            exist_data.longitude = longitude
            exist_data.latitude = latitude
            exist_data.time = location_time
            exist_data1.time = location_time
        else:
            location_add = StudentLocation(longitude, latitude, student_id, location_time)
            db.session.add(location_add)

        db.session.commit()
        return Response(json.dumps({
            'status': True
        }), mimetype='application/json')
    elif request.method == 'GET':
        locations = StudentLocation.query.all()
        result = []
        for location in locations:
            result.append({
                'student_id': location.student_id,
                'longitude': float(location.longitude),
                'latitude': float(location.latitude),
                'attendance': int(location.attendance),
            })
        print(result)
        return Response(json.dumps({
            'status': True,
            'locations': result
        }), mimetype="application/json")


# get& put teacher location
@app.route('/location/teacher', methods=['GET', 'PUT'])
def add_teacher_location():
    if request.method == 'PUT':
        request_data = text_to_dict(str(request.data, encoding='utf-8'))
        print(request_data)
        longitude = request_data['longitude']
        latitude = request_data['latitude']
        teacher_id = request_data['teacher_id']
        location_time = request_data['time']
        print(location_time, type(location_time))
        location_time = time.localtime()
        exist_data = TeacherLocation.query.filter_by(teacher_id=teacher_id).first()
        # [longitude, latitude] = bd_decrypt(latitude, longitude)
        if exist_data:
            exist_data.longitude = longitude
            exist_data.latitude = latitude
            exist_data.time = location_time
        else:
            location_add = TeacherLocation(longitude, latitude, teacher_id, location_time)
            db.session.add(location_add)

        db.session.commit()
        return Response(json.dumps({
            'status': True
        }), mimetype='application/json')
    elif request.method == 'GET':
        locations = TeacherLocation.query.all()
        result = []
        for location in locations:
            result.append({
                'longitude': float(location.longitude),
                'latitude': float(location.latitude),
            })
        return Response(json.dumps({
            'status': True,
            'locations': result
        }), mimetype="application/json")


# get& put teacher location
@app.route('/password/teacher', methods=['PUT'])
def change_teacher_password():
    if request.method == 'PUT':
        request_data = text_to_dict(str(request.data, encoding='utf-8'))
        print(request_data)
        teacher_id = request_data['teacher_id']
        password1 = request_data['password']
        exist_data = Teacher.query.filter_by(teacher_id=teacher_id).first()
        if exist_data:
            exist_data.password = password1
        else:
            password_add = Teacher(teacher_id, password1)
            db.session.add(password_add)

        db.session.commit()
        return Response(json.dumps({
            'status': True
        }), mimetype='application/json')


# get& put teacher location
@app.route('/password/student', methods=['PUT'])
def change_student_password():
    if request.method == 'PUT':
        request_data = text_to_dict(str(request.data, encoding='utf-8'))
        print(request_data)
        student_id = request_data['student_id']
        password2 = request_data['password']
        exist_data = Student.query.filter_by(student_id=student_id).first()
        if exist_data:
            exist_data.password = password2
        else:
            password_add = Student(student_id, password2)
            db.session.add(password_add)

        db.session.commit()
        return Response(json.dumps({
            'status': True
        }), mimetype='application/json')


# get attendance result from database
@app.route('/result/check', methods=['GET'])
def return_result():
    calculate_all()
    calculate_one()
    results = Attendance.query.all()
    attendance = []
    # result = []
    for result in results:
        attendance.append({
            'student_id': result.student_id,
            'attendance': result.attendance,
            'time': str(result.time)
        })
    return Response(json.dumps({
        'status': True,
        'attendance': attendance
    }), mimetype="application/json")


@app.route('/course/check', methods=['GET'])
def return_course():
    courses = TeacherCourse.query.all()
    course = []
    for result in courses:
        course.append({
            'teacher_id': result.teacher_id,
            'classroom': result.classroom,
            'course': result.coursetime
        })
    return Response(json.dumps({
        'status': True,
        'course': course
    }), mimetype="application/json")


@app.route('/course1/check', methods=['GET'])
def return_course1():
    courses = StudentCourse.query.all()
    info = []
    for course in courses:
        info.append({
            'student_id': course.student_id,
            'name': course.name,
            'classroom': course.classroom,
            'teacher': course.teacher,
            'time': course.time
        })
    return Response(json.dumps({
        'status': True,
        'course': info
    }), mimetype="application/json")


def calculate_all():
    teacher_location = TeacherLocation.query.filter_by(teacher_id=1).first()  # Change 1 into variable
    teacher_longitude = teacher_location.longitude
    teacher_latitude = teacher_location.latitude

    student_locations = StudentLocation.query.all()
    for location in student_locations:
        distance = float(calculate(location.longitude, location.latitude, teacher_longitude, teacher_latitude))
        if distance < 0.1:
            location.attendance = 1
        else:
            location.attendance = 0
        db.session.commit()


def calculate_one():
    teacher_location = TeacherLocation.query.filter_by(teacher_id=1).first()  # Change 1 into variable
    teacher_longitude = teacher_location.longitude
    teacher_latitude = teacher_location.latitude

    student_locations = StudentLocation.query.all()
    for location in student_locations:
        distance = calculate(location.longitude, location.latitude, teacher_longitude, teacher_latitude)
        exist_data = Attendance.query.filter_by(student_id=location.student_id).first()
        if not exist_data:
            new_attendance = Attendance(location.student_id, 0)
            db.session.add(new_attendance)
            db.session.commit()
            continue

        if distance < 0.1:
            exist_data.attendance = 1
        else:
            exist_data.attendance = 0
        db.session.commit()


# calculate distance
def calculate(student_longitude, student_latitude, teacher_longitude, teacher_latitude):
    student_longitude, student_latitude, teacher_longitude, teacher_latitude = map(radians, [float(student_longitude),
                                                                                             float(student_latitude),
                                                                                             float(teacher_longitude),
                                                                                             float(teacher_latitude)])
    dlon = abs(teacher_longitude - student_longitude)
    dlat = abs(teacher_latitude - student_latitude)
    a = abs(sin(dlat / 2) ** 2 + cos(student_longitude) * cos(teacher_latitude) * sin(dlon / 2) ** 2)
    b = abs(a)
    distance = abs(2 * asin(sqrt(b)) * 6371 * 1000)
    distance = abs(round(distance / 1000, 3))
    print(distance)
    return float(distance)


# def bd_encrypt(gg_lat, gg_lon):
#     x = float(gg_lon)
#     y = float(gg_lat)
#     z = sqrt(x * x + y * y) + 0.00002 * sin(y * x_pi)
#     theta = atan2(y, x) + 0.000003 * cos(x * x_pi)
#     bd_lon = z * cos(theta) + 0.0065
#     bd_lat = z * sin(theta) + 0.006
#     return bd_lon, bd_lat


# def bd_decrypt(bd_lat, bd_lon):
#     x = float(bd_lon) - 0.0006
#     y = float(bd_lat) - 0.00000008
#     z = sqrt(x * x + y * y) - 0.00005 * sin(y * x_pi)
#     theta = atan2(y, x) - 0.000003 * cos(x * x_pi)
#     gg_lon = z * cos(theta)
#     gg_lat = z * sin(theta)
#     return gg_lon, gg_lat


# Main Function
if __name__ == '__main__':
    app.run(
        host="0.0.0.0",
        port=8082
    )
