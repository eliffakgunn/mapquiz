import 'dart:typed_data';

import 'package:file_picker/file_picker.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_storage_web/firebase_storage_web.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:hexcolor/hexcolor.dart';

import 'country.dart';

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
      title: 'Flutter Demo',
      theme: ThemeData(
        primaryColor: Color(0xff11b719),
      ),
      home: InfoListPage(),
    );
  }
}

class InfoListPage extends StatefulWidget {
  @override
  _InfoListPageState createState() => _InfoListPageState();
}

class _InfoListPageState extends State<InfoListPage> {
  // ignore: non_constant_identifier_names
  var selectedCurrency, selectedCountry, selectedCountry_alert;
  TextEditingController statusController = TextEditingController();
  final FirebaseFirestore _firestore = FirebaseFirestore.instance;
  final Country _country = new Country();
  late List<String> countries = _country.getCountries();
  String selectedCountry2 = "";
  Stream<QuerySnapshot> allInfo =
      FirebaseFirestore.instance.collection("Tüm Bilgiler").snapshots();
  FirebaseStorageWeb _firebaseStorageWeb = FirebaseStorageWeb();

  String alertImg = "";
  String alertInfo = "";
  late Uint8List uploadFile;

  String bilgi1 = "", bilgi2 = "";
  String imgName1 = "", imgName2 = "";

  // ignore: non_constant_identifier_names
  late Uint8List uploadFile_d1, uploadFile_d2;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: HexColor("#d1dbe2"),
      body: Center(
        child: Column(
          mainAxisSize: MainAxisSize.max,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: <Widget>[
            Container(
              height: 100,
              width: 600,
              child: Row(
                mainAxisSize: MainAxisSize.max,
                children: <Widget>[
                  Text(
                    "Ülke:",
                    style: TextStyle(fontSize: 20, color: HexColor("#23435E")),
                    textAlign: TextAlign.center,
                  ),
                  SizedBox(width: 40),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: <Widget>[
                      SizedBox(width: 50.0),
                      DropdownButton(
                        items: countries
                            .map((value) => DropdownMenuItem(
                                  child: Text(
                                    value,
                                    style:
                                        TextStyle(color: HexColor("#23435E")),
                                  ),
                                  value: value,
                                ))
                            .toList(),
                        onChanged: (country) {
                          setState(() {
                            selectedCountry = country;
                            selectedCountry2 = selectedCountry.toString();
                          });
                        },
                        value: selectedCountry,
                        isExpanded: false,
                        hint: Text(
                          'Ülke Seçiniz',
                          style: TextStyle(color: Colors.black54),
                        ),
                      ),
                      SizedBox(
                        width: 100,
                      ),
                      TextButton(
                        onPressed: () {
                          setState(() {
                            bilgi1 = "";
                            bilgi2 = "";
                            imgName1 = "";
                            imgName2 = "";
                          });
                          dialog(context);
                        },
                        child: Text('Ekle',
                            style: TextStyle(
                                color: HexColor("#23435E"), fontSize: 15)),
                      ),
                    ],
                  ),
                ],
              ),
            ),
            Expanded(
              child: Padding(
                padding: const EdgeInsets.only(
                    top: 10, left: 350, bottom: 10, right: 350),
                child: StreamBuilder(
                  stream: selectedCountry2.length == 0
                      ? allInfo
                      : getInfo(selectedCountry),
                  builder: (BuildContext context,
                      AsyncSnapshot<QuerySnapshot> snapshot) {
                    return !snapshot.hasData
                        ? Container(
                            width: 200,
                            height: 200,
                            alignment: Alignment.center,
                            child: Text(
                              "Bilgi bulunamadı.",
                              textAlign: TextAlign.center,
                              style: TextStyle(color: HexColor("#23435E")),
                            ),
                          )
                        : ListView.builder(
                            itemCount: snapshot.data!.docs.length,
                            itemBuilder: (context, index) {
                              DocumentSnapshot mypost =
                                  snapshot.data!.docs[index];

                              return Padding(
                                padding: const EdgeInsets.all(5.0),
                                child: Container(
                                  height: 280,
                                  decoration: BoxDecoration(
                                      color: HexColor("#23435E"),
                                      border: Border.all(
                                          color: HexColor("#23435E"), width: 2),
                                      borderRadius: BorderRadius.all(
                                          Radius.circular(20))),
                                  child: Padding(
                                    padding: const EdgeInsets.all(5.0),
                                    child: Column(
                                      children: [
                                        Text(
                                          selectedCountry2.length == 0
                                              ? mypost['ülke']
                                              : "",
                                          textAlign: TextAlign.center,
                                          style: TextStyle(
                                              color: HexColor("#d1dbe2"),
                                              fontSize: 15,
                                              fontWeight: FontWeight.bold),
                                        ),
                                        SizedBox(
                                          height: 5,
                                        ),
                                        Padding(
                                          padding: const EdgeInsets.only(
                                              left: 130, right: 10),
                                          child: Container(
                                            height: 200,
                                            child: Row(
                                              children: <Widget>[
                                                ClipRRect(
                                                  borderRadius:
                                                      BorderRadius.circular(
                                                          5.0),
                                                  child: FutureBuilder(
                                                    future: _getImage(
                                                        context, mypost['url']),
                                                    builder: (context,
                                                        AsyncSnapshot<Widget?>
                                                            snapshot2) {
                                                      if (snapshot2
                                                              .connectionState ==
                                                          ConnectionState.done)
                                                        return Container(
                                                          height: 150,
                                                          width: 250,
                                                          child: snapshot2.data,
                                                        );

                                                      if (snapshot2
                                                              .connectionState ==
                                                          ConnectionState
                                                              .waiting)
                                                        return Container(
                                                            height: 100,
                                                            width: 100,
                                                            child:
                                                                CircularProgressIndicator());
                                                      return Container();
                                                    },
                                                  ),
                                                ),
                                                SizedBox(
                                                  width: 20,
                                                ),
                                                Container(
                                                  height: 150,
                                                  width: 300,
                                                  alignment: Alignment.center,
                                                  child: Text(
                                                    "${mypost['bilgi']}",
                                                    style: TextStyle(
                                                        fontSize: 16,
                                                        color: HexColor(
                                                            "#d1dbe2")),
                                                    textAlign: TextAlign.center,
                                                  ),
                                                ),
                                              ],
                                            ),
                                          ),
                                        ),
                                        Padding(
                                          padding: const EdgeInsets.only(
                                              left: 750),
                                          child: Row(
                                            children: <Widget>[
                                              InkWell(
                                                  onTap: () {
                                                    editDoc(context, mypost);
                                                  },
                                                  child: Icon(Icons.edit,
                                                      color:
                                                          HexColor("#d1dbe2"),
                                                      size: 30)),
                                              InkWell(
                                                  onTap: () {
                                                    _showChoiseDialog(
                                                        context, mypost.id);
                                                  },
                                                  child: Icon(Icons.delete,
                                                      color:
                                                          HexColor("#d1dbe2"),
                                                      size: 30)),
                                            ],
                                          ),
                                        ),
                                      ],
                                    ),
                                  ),
                                ),
                              );
                            });
                  },
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Future<void> editDoc(BuildContext context, DocumentSnapshot mypost) {
    String title =
        selectedCountry2.length == 0 ? mypost['ülke'] : selectedCountry2;
    TextEditingController controller = TextEditingController();

    return showDialog(
        context: context,
        builder: (BuildContext context) {
          String alertImg2 = mypost['adı'];

          return StatefulBuilder(builder: (context, setState) {
            return AlertDialog(
                title: Text(
                  title,
                  textAlign: TextAlign.center,
                  style: TextStyle(color: HexColor("#23435E")),
                ),
                shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.all(Radius.circular(8.0))),
                content: Container(
                  height: 250,
                  width: 700,
                  decoration: BoxDecoration(
                    color: Colors.white,
                  ),
                  child: Column(
                    children: <Widget>[
                      Padding(
                        padding: const EdgeInsets.all(5.0),
                        child: Container(
                          height: 120,
                          decoration: BoxDecoration(
                              color: Colors.white54,
                              border: Border.all(
                                  color: HexColor("#23435E"), width: 2),
                              borderRadius:
                                  BorderRadius.all(Radius.circular(10))),
                          child: Padding(
                            padding: const EdgeInsets.all(5.0),
                            child: Column(
                                mainAxisAlignment:
                                    MainAxisAlignment.spaceBetween,
                                children: <Widget>[
                                  TextField(
                                      onChanged: (String text) {
                                        setState(() {
                                          alertInfo = text;
                                        });
                                      },
                                      controller: controller,
                                      maxLines: 2,
                                      decoration: InputDecoration(
                                        hintText: mypost['bilgi'],
                                        enabledBorder: UnderlineInputBorder(
                                          borderSide:
                                              BorderSide(color: Colors.white54),
                                        ),
                                        focusedBorder: UnderlineInputBorder(
                                          borderSide:
                                              BorderSide(color: Colors.white54),
                                        ),
                                        border: UnderlineInputBorder(
                                          borderSide:
                                              BorderSide(color: Colors.white54),
                                        ),
                                      ))
                                ]),
                          ),
                        ),
                      ),
                      SizedBox(height: 20),
                      Row(
                        mainAxisSize: MainAxisSize.max,
                        children: <Widget>[
                          Text(
                            "Fotoğraf:",
                            style: TextStyle(
                                fontSize: 20, color: HexColor("#23435E")),
                            textAlign: TextAlign.center,
                          ),
                          SizedBox(width: 20.0),
                          Text(
                            alertImg2,
                            style: TextStyle(
                                fontSize: 15, color: HexColor("#23435E")),
                            textAlign: TextAlign.center,
                          ),
                          SizedBox(
                            width: 30,
                          ),
                          Container(
                            height: 30,
                            width: 120,
                            decoration: BoxDecoration(
                                color: HexColor("#23435E"),
                                borderRadius: BorderRadius.circular(5)),
                            child: FlatButton(
                              onPressed: () async {
                                var newImg = pickImage();
                                newImg.then((value) => {
                                      setState(() {
                                        alertImg2 = value;
                                      })
                                    });
                              },
                              child: Text(
                                'Değiştir',
                                style: TextStyle(
                                    color: Colors.white, fontSize: 15),
                              ),
                            ),
                          ),
                        ],
                      ),
                      SizedBox(
                        height: 40,
                      ),
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: <Widget>[
                          GestureDetector(
                            onTap: () {
                              Navigator.pop(context);
                            },
                            child: Text(
                              "Vazgeç",
                              style: TextStyle(
                                  color: HexColor("#23435E"),
                                  fontWeight: FontWeight.bold),
                            ),
                          ),
                          GestureDetector(
                            onTap: () {
                              saveInfo(mypost.id, mypost['adı']);
                              controller.clear();
                              Navigator.pop(context);
                            },
                            child: Text(
                              "Kaydet",
                              style: TextStyle(
                                  color: HexColor("#23435E"),
                                  fontWeight: FontWeight.bold),
                            ),
                          ),
                        ],
                      )
                    ],
                  ),
                ));
          });
        });
  }

  deleteDoc(String docId) {
    DocumentReference docRef;

    if (selectedCountry2.length == 0) {
      docRef = _firestore.collection("Tüm Bilgiler").doc(docId);

      docRef.get().then((value) {
        Map<String, dynamic> data = value.data();
        String doc = data['docRef'];
        String country = data['ülke'];

        _firestore
            .collection("Bilgiler")
            .doc(country)
            .collection(country)
            .doc(doc)
            .delete();

        docRef.delete();
      });
    } else {
      docRef = _firestore
          .collection("Bilgiler")
          .doc(selectedCountry)
          .collection(selectedCountry)
          .doc(docId);

      docRef.get().then((value) {
        Map<String, dynamic> data = value.data();
        String doc = data['docRef'];
        _firestore.collection("Tüm Bilgiler").doc(doc).delete();

        docRef.delete();
      });
    }
  }

  Future<void> _showChoiseDialog(BuildContext context, String docId) {
    return showDialog(
        context: context,
        builder: (BuildContext context) {
          return AlertDialog(
              title: Text("Silmek istediğinize emin misiniz?",
                  textAlign: TextAlign.center,
                  style: TextStyle(
                    color: HexColor("#23435E"),
                  )),
              shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.all(Radius.circular(8.0))),
              content: Container(
                  height: 30,
                  decoration: BoxDecoration(
                    color: Colors.white,
                  ),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: <Widget>[
                      GestureDetector(
                        onTap: () {
                          deleteDoc(docId);
                          Navigator.pop(context);
                        },
                        child: Text(
                          "Evet",
                          style: TextStyle(
                              color: HexColor("#23435E"),
                              fontWeight: FontWeight.bold),
                        ),
                      ),
                      GestureDetector(
                        onTap: () {
                          Navigator.pop(context);
                        },
                        child: Text(
                          "Vazgeç",
                          style: TextStyle(
                              color: HexColor("#23435E"),
                              fontWeight: FontWeight.bold),
                        ),
                      ),
                    ],
                  )));
        });
  }

  Future<void> dialog(BuildContext context) async {
    return showDialog(
        context: context,
        builder: (BuildContext context) {
          var selectedCountry_alert = null;
          String textHolder1 = 'Henüz fotoğraf seçmediniz.';

          return StatefulBuilder(builder: (context, setState) {
            return AlertDialog(
                shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.all(Radius.circular(8.0))),
                content: Container(
                  height: selectedCountry2.length == 0 ? 450 : 300,
                  width: 500,
                  decoration: BoxDecoration(
                    color: Colors.white,
                  ),
                  child: Center(
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      crossAxisAlignment: CrossAxisAlignment.center,
                      children: <Widget>[
                        selectedCountry2.length == 0
                            ? SizedBox(
                                height: 5,
                              )
                            : SizedBox(),
                        selectedCountry2.length == 0
                            ? Container(
                                height: 100,
                                child: Row(
                                  children: <Widget>[
                                    Text(
                                      "Ülke:",
                                      style: TextStyle(
                                          fontSize: 20,
                                          color: HexColor("#23435E")),
                                      textAlign: TextAlign.center,
                                    ),
                                    SizedBox(width: 40),
                                    StreamBuilder<QuerySnapshot>(
                                        stream: _firestore
                                            .collection("Bilgiler")
                                            .snapshots(),
                                        builder: (context, snapshot) {
                                          if (!snapshot.hasData)
                                            return Text("Yükleniyor.....");
                                          else {
                                            List<DropdownMenuItem>
                                                currencyItems = [];
                                            for (int i = 0;
                                                i < snapshot.data!.docs.length;
                                                i++) {
                                              DocumentSnapshot snap =
                                                  snapshot.data!.docs[i];
                                              currencyItems.add(
                                                DropdownMenuItem(
                                                  child: Text(
                                                    snap.id,
                                                    style: TextStyle(
                                                        color: HexColor(
                                                            "#23435E")),
                                                  ),
                                                  value: "${snap.id}",
                                                ),
                                              );
                                            }
                                            return Row(
                                              children: <Widget>[
                                                SizedBox(width: 50.0),
                                                DropdownButton(
                                                  items: countries
                                                      .map((value) =>
                                                          DropdownMenuItem(
                                                            child: Text(
                                                              value,
                                                              style: TextStyle(
                                                                  color: HexColor(
                                                                      "#23435E")),
                                                            ),
                                                            value: value,
                                                          ))
                                                      .toList(),
                                                  onChanged: (country) {
                                                    setState(() {
                                                      selectedCountry_alert =
                                                          country;
                                                    });
                                                  },
                                                  value: selectedCountry_alert,
                                                  isExpanded: false,
                                                  hint: Text(
                                                    'Ülke Seçiniz',
                                                    style: TextStyle(
                                                        color: Colors.black54),
                                                  ),
                                                ),
                                              ],
                                            );
                                          }
                                        }),
                                  ],
                                ),
                              )
                            : SizedBox(),
                        selectedCountry2.length == 0
                            ? SizedBox(
                                height: 20,
                              )
                            : SizedBox(),
                        Container(
                          height: 100,
                          child: Row(
                            children: <Widget>[
                              Text(
                                "Bilgi:",
                                style: TextStyle(
                                    fontSize: 20, color: HexColor("#23435E")),
                                textAlign: TextAlign.center,
                              ),
                              SizedBox(width: 80.0),
                              SizedBox(
                                  height: 50,
                                  width: 350,
                                  child: TextField(
                                      onChanged: (String text) {
                                        setState(() {
                                          bilgi1 = text;
                                        });
                                      },
                                      controller: statusController,
                                      maxLines: 2,
                                      decoration: InputDecoration(
                                        hintText: 'Bilgi giriniz',
                                        enabledBorder: UnderlineInputBorder(
                                          borderSide: BorderSide(
                                              color: HexColor("#23435E")),
                                        ),
                                        focusedBorder: UnderlineInputBorder(
                                          borderSide: BorderSide(
                                              color: HexColor("#23435E")),
                                        ),
                                        border: UnderlineInputBorder(
                                          borderSide: BorderSide(
                                              color: HexColor("#23435E")),
                                        ),
                                      ))),
                            ],
                          ),
                        ),
                        SizedBox(height: 20),
                        Container(
                          height: 100,
                          width: 600,
                          alignment: Alignment.center,
                          child: Row(
                            children: <Widget>[
                              Text(
                                "Fotoğraf:",
                                style: TextStyle(
                                    fontSize: 20, color: HexColor("#23435E")),
                                textAlign: TextAlign.center,
                              ),
                              SizedBox(width: 50.0),
                              Text(
                                "$textHolder1",
                                style: TextStyle(
                                    fontSize: 15, color: HexColor("#23435E")),
                                textAlign: TextAlign.center,
                              )
                            ],
                          ),
                        ),
                        SizedBox(height: 20),
                        Container(
                          height: 50,
                          //width: 310,
                          child: Row(
                            children: <Widget>[
                              Container(
                                height: 50,
                                width: 180,
                                decoration: BoxDecoration(
                                    color: HexColor("#23435E"),
                                    borderRadius: BorderRadius.circular(25)),
                                child: FlatButton(
                                  onPressed: () async {
                                    var newImg = pickImageDialog();
                                    newImg.then((value) => {
                                          setState(() {
                                            textHolder1 = value;
                                          })
                                        });
                                  },
                                  child: Text(
                                    'Fotoğraf Ekle',
                                    style: TextStyle(
                                        color: HexColor("#d1dbe2"),
                                        fontSize: 18),
                                  ),
                                ),
                              ),
                              SizedBox(width: 10),
                              Container(
                                height: 50,
                                width: 150,
                                decoration: BoxDecoration(
                                    color: HexColor("#23435E"),
                                    borderRadius: BorderRadius.circular(25)),
                                child: FlatButton(
                                  onPressed: () async {
                                    print("kaydete basti");
                                    selectedCountry2.length == 0
                                        ? addInfo(selectedCountry_alert)
                                        : addInfo(selectedCountry2);
                                  },
                                  child: Text(
                                    'Kaydet',
                                    style: TextStyle(
                                        color: HexColor("#d1dbe2"),
                                        fontSize: 18),
                                  ),
                                ),
                              ),
                              SizedBox(width: 10),
                              Container(
                                height: 50,
                                width: 150,
                                decoration: BoxDecoration(
                                    color: HexColor("#23435E"),
                                    borderRadius: BorderRadius.circular(25)),
                                child: FlatButton(
                                  onPressed: () async {
                                    statusController.clear();
                                    Navigator.pop(context);
                                  },
                                  child: Text(
                                    'İptal',
                                    style: TextStyle(
                                        color: HexColor("#d1dbe2"),
                                        fontSize: 18),
                                  ),
                                ),
                              ),
                            ],
                          ),
                        ),
                      ],
                    ),
                  ),
                ));
          });
        });
  }

  Future<void> addInfo(String country) async {
    print("addInfo");

    CollectionReference ref, ref2 = _firestore.collection('Tüm Bilgiler');

    String sc, info, img, url;
    Uint8List ut;
    sc = country;
    info = bilgi1;
    img = imgName1;

    if (sc == null) {
      print("++ if");
      Fluttertoast.showToast(
          msg: 'Lütfen bir ülke seçin',
          toastLength: Toast.LENGTH_SHORT,
          webPosition: "center",
          webBgColor: "ff90a4ae");
    } else if (bilgi1.length == 0) {
      print("++ else if 3");
      Fluttertoast.showToast(
          msg: 'Lütfen bilgi girin',
          toastLength: Toast.LENGTH_SHORT,
          webPosition: "center",
          webBgColor: "ff90a4ae");
    } else if (imgName1.length == 0) {
      print("++ else if 2");
      Fluttertoast.showToast(
          msg: 'Lütfen fotoğraf ekleyin',
          toastLength: Toast.LENGTH_SHORT,
          webPosition: "center",
          webBgColor: "ff90a4ae");
    } else {
      print("++ else");
      ut = uploadFile_d1;
      ref = _firestore.collection('Bilgiler').doc(sc).collection(sc);
      var refS =
          _firebaseStorageWeb.ref('gs://mapquiz-b1b3a.appspot.com').child(img);
      var task = refS.putData(ut);

      task.snapshotEvents.listen((event) {});
      await task.onComplete;

      url = await task.snapshot.ref.getDownloadURL();

      var documentRef = ref2.add(
          {'bilgi': info, 'url': url, 'adı': img, 'ülke': sc}).then((value1) {
        ref.add({
          'bilgi': info,
          'url': url,
          'adı': img,
          'docRef': value1.id
        }).then((value2) {
          value1.update({'docRef': value2.id});
        });
      });

      // ignore: unnecessary_null_comparison
      if (documentRef != null) {
        Fluttertoast.showToast(
            msg: 'Başarıyla kaydedildi.',
            toastLength: Toast.LENGTH_SHORT,
            webPosition: "center",
            webBgColor: "ff90a4ae");
      }

      statusController.clear();
      Navigator.pop(context);
    }
  }

  Future<String> pickImageDialog() async {
    FilePickerResult result;
    String name = "";

    result = await FilePicker.platform
        .pickFiles(type: FileType.image, allowedExtensions: ['jpg', 'png']);

    if (result != null) {
      Uint8List uploadFile2 = result.files.first.bytes;
      String fileName = result.files.single.name;
      name = (DateTime.now().millisecondsSinceEpoch).toString() +
          "." +
          fileName.split('.').last;

      setState(() {
        imgName1 = name;
        uploadFile_d1 = uploadFile2;
      });
    }

    return name;
  }

  saveInfo(String docId, String img) async {
    if (selectedCountry2.length == 0) {
      var docRef = _firestore.collection("Tüm Bilgiler").doc(docId);

      docRef.get().then((value) async {
        Map<String, dynamic> data = value.data();
        String doc = data['docRef'];
        String country = data['ülke'];

        var docRef2 = _firestore
            .collection("Bilgiler")
            .doc(country)
            .collection(country)
            .doc(doc);

        if (alertInfo.length != 0) {
          docRef.update({'bilgi': alertInfo});
          docRef2.update({'bilgi': alertInfo});
        }

        if (alertImg != img) {
          var ref2 = _firebaseStorageWeb
              .ref('gs://mapquiz-b1b3a.appspot.com')
              .child(alertImg);
          var task = ref2.putData(uploadFile);

          task.snapshotEvents.listen((event) {});
          await task.onComplete;

          String url = await task.snapshot.ref.getDownloadURL();
          docRef.update({'url': url});
          docRef2.update({'url': url});
          docRef.update({'adı': alertImg});
          docRef2.update({'adı': alertImg});
        }
      });
    } else {
      var docRef = _firestore
          .collection("Bilgiler")
          .doc(selectedCountry)
          .collection(selectedCountry)
          .doc(docId);

      docRef.get().then((value) async {
        Map<String, dynamic> data = value.data();
        String doc = data['docRef'];

        var docRef2 = _firestore.collection("Tüm Bilgiler").doc(doc);

        if (alertInfo.length != 0) {
          docRef.update({'bilgi': alertInfo});
          docRef2.update({'bilgi': alertInfo});
        }

        if (alertImg != img) {
          var ref2 = _firebaseStorageWeb
              .ref('gs://mapquiz-b1b3a.appspot.com')
              .child(alertImg);
          var task = ref2.putData(uploadFile);

          task.snapshotEvents.listen((event) {});
          await task.onComplete;

          String url = await task.snapshot.ref.getDownloadURL();
          docRef.update({'url': url});
          docRef2.update({'url': url});
          docRef.update({'adı': alertImg});
          docRef2.update({'adı': alertImg});
        }
      });
    }
  }

  Future<String> pickImage() async {
    FilePickerResult result;
    String name = "";

    result = await FilePicker.platform
        .pickFiles(type: FileType.image, allowedExtensions: ['jpg', 'png']);

    if (result != null) {
      Uint8List uploadFile2 = result.files.first.bytes;
      String fileName = result.files.single.name;
      name = (DateTime.now().millisecondsSinceEpoch).toString() +
          "." +
          fileName.split('.').last;

      setState(() {
        alertImg = name;
        uploadFile = uploadFile2;
      });
    }

    return name;
  }

  Stream<QuerySnapshot> getInfo(String country) {
    var ref;
    ref = _firestore
        .collection("Bilgiler")
        .doc(country)
        .collection(country)
        .snapshots();
    return ref == null ? null : ref;
  }

  Future<void> editInfo(String docId) async {
    var ref = _firestore.collection("Bilgiler").doc(docId);
  }

  Future<Widget> _getImage(BuildContext context, String downloadUrl) async {
    Image m = Image.network(downloadUrl);
    return m;
  }
}
