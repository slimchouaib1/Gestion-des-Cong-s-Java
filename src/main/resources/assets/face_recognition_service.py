import boto3
import pymysql
import base64

def get_user_face_data(user_id):
    connection = pymysql.connect(host='127.0.0.1',
                                 user='root',
                                 password='',
                                 db='bfpmeconge')
    try:
        with connection.cursor() as cursor:
            sql = "SELECT face_data1, face_data2, face_data3, face_data4 FROM user WHERE id = %s"
            cursor.execute(sql, (user_id,))
            result = cursor.fetchone()
            if result:
                return result
    finally:
        connection.close()
    return None

def compare_face_with_stored(user_id, new_image_path):
    stored_face_data = get_user_face_data(user_id)
    if stored_face_data:
        client = boto3.client('rekognition')
        with open(new_image_path, 'rb') as new_image:
            new_image_bytes = new_image.read()

        for face_data in stored_face_data:
            if face_data:
                stored_image_bytes = base64.b64decode(face_data)

                response = client.compare_faces(
                    SourceImage={'Bytes': new_image_bytes},
                    TargetImage={'Bytes': stored_image_bytes},
                    SimilarityThreshold=80
                )

                if response['FaceMatches']:
                    return True
    return False

def login_controller(user_id, new_image_path):
    if compare_face_with_stored(user_id, new_image_path):
        return "Login successful"
    else:
        return "Login failed"
