import 'package:firebase_core/firebase_core.dart';
import 'package:flutter/material.dart';
import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter_mapquiz/main.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:hexcolor/hexcolor.dart';

import 'quiz.dart';
import 'login.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp();
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: QuizHomePage(),
    );
  }
}

class QuizHomePage extends StatefulWidget {
  @override
  _QuizHomePageState createState() => _QuizHomePageState();
}

class _QuizHomePageState extends State<QuizHomePage> {
  TextEditingController statusController = TextEditingController();
  User _userCredential = FirebaseAuth.instance.currentUser;

  String password = '', username = '';
  String _userName = "", _points = "";

  Future<bool> onBackPressed() {
    Navigator.push(context, MaterialPageRoute(builder: (_) => HomePage()));
  }

  @override
  Widget build(BuildContext context) {
    setFields();

    return WillPopScope(
      onWillPop: onBackPressed,
      child: Scaffold(
        backgroundColor: HexColor("#d1dbe2"),
        body: Center(
          child: Column(
            mainAxisSize: MainAxisSize.max,
            crossAxisAlignment: CrossAxisAlignment.center,
            children: <Widget>[
              SizedBox(
                height: 50,
              ),
              Container(
                alignment: Alignment.center,
                height: 200,
                width: 200,
                child: Image.asset("images/quiz_home_icon.png"),
              ),
              SizedBox(
                height: 30,
              ),
              Container(
                alignment: Alignment.center,
                child: Text(
                  _userName,
                  style: TextStyle(fontSize: 30, color: HexColor("#23435E")),
                  textAlign: TextAlign.center,
                ),
              ),
              SizedBox(
                height: 5,
              ),
              Container(
                alignment: Alignment.center,
                height: 30,
                width: 120,
                child: Padding(
                  padding: const EdgeInsets.only(left: 5, right: 5),
                  child: Row(
                    children: <Widget>[
                      Container(
                        height: 30,
                        width: 30,
                        child: Image.asset("images/coin.png"),
                      ),
                      Text(
                        "$_points puan",
                        style:
                            TextStyle(fontSize: 15, color: HexColor("#23435E")),
                        textAlign: TextAlign.center,
                      ),
                    ],
                  ),
                ),
              ),
              SizedBox(
                height: 90,
              ),
              Container(
                height: 60,
                width: 210,
                decoration: BoxDecoration(
                    color: HexColor("#23435E"),
                    borderRadius: BorderRadius.circular(5)),
                child: FlatButton(
                  onPressed: () async {
                    Navigator.push(
                        context, MaterialPageRoute(builder: (_) => QuizPage()));
                  },
                  child: Text(
                    'Oyna',
                    style: TextStyle(color: HexColor("#d1dbe2"), fontSize: 22),
                  ),
                ),
              ),
              SizedBox(height: 20),
              Container(
                height: 60,
                width: 210,
                decoration: BoxDecoration(
                    color: HexColor("#23435E"),
                    borderRadius: BorderRadius.circular(5)),
                child: FlatButton(
                  onPressed: () async {
                    /*Navigator.push(context,
                        MaterialPageRoute(builder: (_) => LoginPage()));*/
                    if (_userCredential == null) {
                      Navigator.push(context,
                          MaterialPageRoute(builder: (_) => LoginPage()));
                    }else{
                      Navigator.push(context,
                          MaterialPageRoute(builder: (_) => QuizPage()));
                    }
                  },
                  child: Text(
                    'Grupla Oyna',
                    style: TextStyle(color: HexColor("#d1dbe2"), fontSize: 22),
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  void setFields() {
    if (_userCredential == null) {
      setState(() {
        _userName = "Misafir";
        _points = "0";
      });
    } else {
      var docRef = FirebaseFirestore.instance
          .collection("Kullanıcılar")
          .doc(_userCredential.uid);

      if (docRef != null) {
        docRef.get().then((value) {
          Map<String, dynamic> data = value.data();
          String username = data['kullaniciAdi'];
          int puan = data['puan'];
          setState(() {
            _userName = username;
            _points = puan.toString();
          });
        });
      }
    }
  }
}
