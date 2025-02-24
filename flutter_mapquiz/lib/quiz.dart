import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter_mapquiz/quiz_home.dart';
import 'package:hexcolor/hexcolor.dart';
import 'package:flutter/material.dart';

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
      home: QuizPage(),
    );
  }
}

class QuizPage extends StatefulWidget {
  @override
  _QuizPageState createState() => _QuizPageState();
}

class _QuizPageState extends State<QuizPage> {
  User _userCredential = FirebaseAuth.instance.currentUser;

  Future<bool> onBackPressed() {
    Navigator.push(context, MaterialPageRoute(builder: (_) => QuizHomePage()));
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: onBackPressed,
      child: Scaffold(
        backgroundColor: HexColor("#d1dbe2"),
        body: SingleChildScrollView(
          child: Column(
            children: <Widget>[
              Padding(
                padding: const EdgeInsets.only(top: 100, left: 15, bottom: 30),
                child: Container(
                  alignment: Alignment.center,
                  child: Text(
                    "Quize Hoşgeldiniz!",
                    style: TextStyle(fontSize: 40, color: HexColor("#23435E")),
                    textAlign: TextAlign.center,
                  ),
                ),
              ),
              SizedBox(
                height: 20,
              ),
              Container(
                height: 50,
                width: 150,
                decoration: BoxDecoration(
                    color: HexColor("#23435E"),
                    borderRadius: BorderRadius.circular(5)),
                child: FlatButton(
                  onPressed: () async {
                    Navigator.push(context,
                        MaterialPageRoute(builder: (_) => QuizHomePage()));
                  },
                  child: Text(
                    'Anasayfa',
                    style: TextStyle(color: HexColor("#d1dbe2"), fontSize: 19),
                  ),
                ),
              ),
              SizedBox(
                height: 20,
              ),
              _userCredential != null
                  ? Container(
                      height: 50,
                      width: 150,
                      decoration: BoxDecoration(
                          color: HexColor("#23435E"),
                          borderRadius: BorderRadius.circular(5)),
                      child: FlatButton(
                        onPressed: () async {
                          signOut();
                        },
                        child: Text(
                          'Çıkış Yap',
                          style: TextStyle(
                              color: HexColor("#d1dbe2"), fontSize: 19),
                        ),
                      ),
                    )
                  : Container(),
            ],
          ),
        ),
      ),
    );
  }

  void signOut() {
    FirebaseAuth.instance.signOut().then((value) => Navigator.push(context, MaterialPageRoute(builder: (_) => QuizHomePage())));
    //Navigator.push(context, MaterialPageRoute(builder: (_) => QuizHomePage()));
  }
}
