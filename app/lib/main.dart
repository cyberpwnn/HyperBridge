import 'dart:convert';
import 'dart:ui';

import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:tinycolor/tinycolor.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'Flutter Demo',
      theme: ThemeData(
          canvasColor: Colors.black,
          bottomAppBarColor: Colors.black,
          iconTheme: IconThemeData(
            color: Colors.white,
          ),
          appBarTheme:
              AppBarTheme(color: Colors.black, shadowColor: Colors.black),
          primarySwatch: Colors.deepPurple,
          brightness: Brightness.dark),
      home: HyperBridge(),
    );
  }
}

class HyperBridge extends StatefulWidget {
  @override
  _HyperBridgeState createState() => _HyperBridgeState();
}

class _HyperBridgeState extends State<HyperBridge> {
  int index = 1;

  Widget lights(BuildContext context) {
    return Stack(
      children: [
        FutureBuilder<List<String>>(
          future: Network.getGroups(),
          builder: (context, snap) {
            if (!snap.hasData) {
              return Center(
                child: CircularProgressIndicator(
                  valueColor: AlwaysStoppedAnimation(Colors.deepPurple),
                ),
              );
            }

            if (snap.data.length == 0) {
              return Center(
                child: Text(
                  "No Groups",
                  style: TextStyle(fontSize: 24),
                ),
              );
            }

            return GridView.builder(
              itemCount: snap.data.length,
              gridDelegate: SliverGridDelegateWithMaxCrossAxisExtent(
                  maxCrossAxisExtent: 200),
              itemBuilder: (context, pos) => InkWell(
                onTap: () => Navigator.push(
                        context,
                        new MaterialPageRoute(
                            builder: (context) => EditGroup(snap.data[pos])))
                    .then((value) => setState(() {})),
                child: Container(
                  width: 200,
                  height: 200,
                  child: FutureBuilder<GroupData>(
                    future: Network.getGroup(snap.data[pos]),
                    builder: (context, snap2) {
                      if (!snap2.hasData) {
                        return Center(
                          child: CircularProgressIndicator(
                            valueColor:
                                AlwaysStoppedAnimation(Colors.deepPurple),
                          ),
                        );
                      }

                      return Center(
                        child: Column(
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            Text(
                              snap2.data.name,
                              style: TextStyle(fontSize: 24),
                            ),
                            Text(snap2.data.lights.length.toString() +
                                " Lights"),
                            Text(snap2.data.watts.round().toString() + "W"),
                          ],
                        ),
                      );
                    },
                  ),
                ),
              ),
            );
          },
        ),
        Align(
          alignment: Alignment.bottomRight,
          child: Padding(
            child: FloatingActionButton(
              child: Icon(Icons.add),
              tooltip: "Add Group",
              backgroundColor: Colors.deepPurple,
              onPressed: () => Network.createGroup("New Group")
                  .then((value) => Navigator.push(
                      context,
                      MaterialPageRoute(
                          builder: (context) => EditGroup(value.id))))
                  .then((value) => setState(() {})),
            ),
            padding: EdgeInsets.only(right: 14),
          ),
        )
      ],
    );
  }

  Widget haus(BuildContext context) {
    return Text("Haus");
  }

  Widget more(BuildContext context) {
    return Text("More");
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: index == 0
          ? lights(context)
          : index == 1
              ? haus(context)
              : more(context),
      bottomNavigationBar: BottomNavigationBar(
        selectedItemColor: Colors.deepPurple,
        currentIndex: index,
        onTap: (m) => setState(() {
          index = m;
        }),
        items: [
          BottomNavigationBarItem(icon: Icon(Icons.lightbulb), label: "Lights"),
          BottomNavigationBarItem(icon: Icon(Icons.home_filled), label: "Haus"),
          BottomNavigationBarItem(icon: Icon(Icons.settings), label: "More")
        ],
      ),
    );
  }
}

class EditGroup extends StatefulWidget {
  final String id;

  EditGroup(this.id);

  @override
  _EditGroupState createState() => _EditGroupState();
}

class _EditGroupState extends State<EditGroup> {
  TextEditingController tc = new TextEditingController();

  @override
  Widget build(BuildContext context) {
    return FutureBuilder<GroupData>(
      future: Network.getGroup(widget.id),
      builder: (context, snap) {
        if (!snap.hasData) {
          return Scaffold(
            body: Center(
              child: CircularProgressIndicator(
                valueColor: AlwaysStoppedAnimation(Colors.deepPurple),
              ),
            ),
          );
        }

        tc.text = snap.data.name;

        return Scaffold(
          appBar: AppBar(
            actions: [
              IconButton(
                icon: Icon(Icons.add),
                tooltip: "Add Light",
                onPressed: () => Navigator.push(context,
                        MaterialPageRoute(builder: (context) => LightPicker()))
                    .then((value) => Network.addLightToGroup(value, widget.id)
                        .then((value) => setState(() {}))),
              )
            ],
            title: TextField(
              controller: tc,
              onSubmitted: (v) => Network.setGroupName(widget.id, v),
              onChanged: (v) => Network.setGroupName(widget.id, v),
            ),
          ),
          body: GridView.builder(
            itemCount: snap.data.lights.length,
            gridDelegate: SliverGridDelegateWithMaxCrossAxisExtent(
                maxCrossAxisExtent: 200),
            itemBuilder: (context, pos) => InkWell(
              onTap: () {},
              child: Container(
                width: 200,
                height: 200,
                child: FutureBuilder<LightData>(
                  future: Network.getLight(snap.data.lights[pos]),
                  builder: (context, snap2) {
                    if (!snap2.hasData) {
                      return Center(
                        child: CircularProgressIndicator(
                          valueColor: AlwaysStoppedAnimation(Colors.deepPurple),
                        ),
                      );
                    }

                    return Center(
                      child: Column(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          Text(
                            snap2.data.name,
                            style: TextStyle(fontSize: 24),
                          ),
                          Text(snap2.data.watts.round().toString() + "W"),
                        ],
                      ),
                    );
                  },
                ),
              ),
            ),
          ),
        );
      },
    );
  }
}

class LightPicker extends StatefulWidget {
  @override
  _LightPickerState createState() => _LightPickerState();
}

class _LightPickerState extends State<LightPicker> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: FutureBuilder<List<String>>(
        future: Network.getLights(),
        builder: (context, snp) {
          if (!snp.hasData) {
            return Center(
              child: CircularProgressIndicator(
                valueColor: AlwaysStoppedAnimation(Colors.deepPurple),
              ),
            );
          }

          return GridView.builder(
            itemCount: snp.data.length,
            gridDelegate: SliverGridDelegateWithMaxCrossAxisExtent(
                maxCrossAxisExtent: 200),
            itemBuilder: (context, pos) => InkWell(
              onTap: () => Navigator.pop(context, snp.data[pos]),
              child: Container(
                width: 200,
                height: 200,
                child: FutureBuilder<LightData>(
                  future: Network.getLight(snp.data[pos]),
                  builder: (context, snap2) {
                    if (!snap2.hasData) {
                      return Center(
                        child: CircularProgressIndicator(
                          valueColor: AlwaysStoppedAnimation(Colors.deepPurple),
                        ),
                      );
                    }

                    return Center(
                      child: Column(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          Text(
                            snap2.data.name,
                            style: TextStyle(fontSize: 24),
                          ),
                          Text(snap2.data.watts.round().toString() + "W"),
                        ],
                      ),
                    );
                  },
                ),
              ),
            ),
          );
        },
      ),
    );
  }
}

class LightView extends StatefulWidget {
  final String id;

  LightView({this.id});

  @override
  _LightViewState createState() => _LightViewState();
}

class _LightViewState extends State<LightView> {
  LightData temp;
  int last = DateTime.now().millisecondsSinceEpoch;

  Future<LightData> get() async {
    if (temp != null) {
      return temp;
    }

    return Network.getLight(widget.id);
  }

  @override
  Widget build(BuildContext context) {
    return FutureBuilder<LightData>(
      future: get(),
      builder: (context, snap) {
        if (!snap.hasData) {
          return Container();
        }
        temp = null;
        LightData d = snap.data;
        int nightlightmode = 60;
        TinyColor buffer = TinyColor(d.getColor());
        return Padding(
          padding: EdgeInsets.all(7),
          child: InkWell(
            child: GestureDetector(
              onPanUpdate: (a) {
                buffer = buffer.spin(a.delta.dx).saturate(a.delta.dy.toInt());
                d.r = buffer.color.red;
                d.g = buffer.color.green;
                d.b = buffer.color.blue;
                if (DateTime.now().millisecondsSinceEpoch - last < 250) {
                  setState(() {
                    temp = d;
                  });
                } else {
                  last = DateTime.now().millisecondsSinceEpoch;
                  Network.setColor(d.id,
                          color: buffer.color,
                          brightness: d.a / 255.0,
                          now: true,
                          ms: 250)
                      .then((value) => setState(() {}));
                }
              },
              onPanEnd: (b) {
                Future.delayed(
                    Duration(milliseconds: 500),
                    () => Network.setColor(d.id,
                            color: buffer.color,
                            brightness: d.a / 255.0,
                            ms: 1000)
                        .then((value) => setState(() {})));
              },
              child: Container(
                width: 200,
                height: 200,
                child: Center(
                  child: Stack(
                    alignment: Alignment.center,
                    children: [
                      Align(
                        alignment: Alignment.topCenter,
                        child: Text(
                            d.a > nightlightmode ? " \u{e811} " : " \u{f2ab} ",
                            textAlign: TextAlign.center,
                            style: TextStyle(
                                fontFamily: "MaterialIcons",
                                color: d.getColor(alphaBrightness: true),
                                fontSize: 45,
                                height: 1.7,
                                shadows: <Shadow>[
                                  Shadow(
                                    color: d.getColor(alphaBrightness: true),
                                    blurRadius: lerpDouble(
                                        0,
                                        40,
                                        (d.a > nightlightmode
                                            ? (d.a.toDouble() / 255.0)
                                            : (d.a.toDouble() /
                                                nightlightmode.toDouble()))),
                                  )
                                ])),
                      ),
                      Align(
                        alignment: Alignment.bottomCenter,
                        child: Text(
                          d.name,
                          textAlign: TextAlign.center,
                          style: TextStyle(fontSize: 24),
                        ),
                      )
                    ],
                  ),
                ),
              ),
            ),
            onTap: () {
              Network.setColorRaw(d.id,
                      r: d.r, g: d.g, b: d.b, a: d.a > 60 ? 30 : 255, ms: 250)
                  .then((value) => setState(() {}));
            },
          ),
        );
      },
    );
  }
}

class Network {
  static String address = "192.168.50.78";
  static int port = 13369;
  static String url = "http://$address:$port";

  static Future<LightPower> getPower() async =>
      LightPower.fromJSON((await get("$url/getpower")));

  static Future<List<String>> getLights() async {
    List<dynamic> data = ((await get("$url/getlights"))["lights"]);
    List<String> dartIsTheShittiestLanguageOnThePlanet = List<String>();

    for (int i = 0; i < data.length; i++) {
      dartIsTheShittiestLanguageOnThePlanet.add(data[i].toString());
    }

    return dartIsTheShittiestLanguageOnThePlanet;
  }

  static Future<List<String>> getGroups() async {
    List<dynamic> data = ((await get("$url/getgroups"))["groups"]);
    List<String> dartIsTheShittiestLanguageOnThePlanet = List<String>();

    for (int i = 0; i < data.length; i++) {
      dartIsTheShittiestLanguageOnThePlanet.add(data[i].toString());
    }

    return dartIsTheShittiestLanguageOnThePlanet;
  }

  static Future<LightData> getLight(String id) async =>
      LightData.fromJSON((await get("$url/getlight?id=$id"))["light"]);

  static Future<GroupData> getGroup(String id) async =>
      GroupData.fromJSON((await get("$url/getgroup?id=$id"))["group"]);

  static Future<GroupData> createGroup(String name) async =>
      GroupData.fromJSON((await get("$url/creategroup?name=$name"))["group"]);

  static Future<bool> addLightToGroup(String light, String group) async =>
      (await get(
          '$url/addlight?d={"light":"$light","group":"$group"}'))["type"] ==
      "ok";

  static Future<bool> removeLightFromGroup(String light, String group) async =>
      (await get(
          '$url/removelight?d={"light":"$light","group":"$group"}'))["type"] ==
      "ok";

  static Future<bool> setGroupName(String id, String name) async =>
      (await get('$url/setgroupname?d={"id":"$id","name":"$name"}'))["type"] ==
      "ok";

  static Future<bool> removeGroup(String group) async =>
      (await get('$url/removegroup?id=$group'))["type"] == "ok";

  static Future<bool> setColor(String id,
          {Color color = Colors.white54,
          double brightness = 0.8,
          bool now = false,
          int ms = 1000}) =>
      setColorRaw(id,
          r: color.red,
          g: color.green,
          b: color.blue,
          a: (brightness * 255).round(),
          ms: ms);

  static Future<bool> setColorRaw(String id,
          {int r = 255,
          int g = 255,
          int b = 255,
          int a = 160,
          bool now = false,
          int ms = 1000}) async =>
      (await get(
              '$url/setcolor?d={"id":"$id","r":$r,"g":$g,"b":$b,"a":$a,"t":$ms,"now":$now}'))[
          "type"] ==
      "ok";

  static Future<Map<String, dynamic>> get(String url) async {
    try {
      String s = (await (Dio()
                ..options = new BaseOptions(responseType: ResponseType.plain))
              .get(url))
          .data
          .toString();
      print("$url -> $s");
      return jsonDecode(s);
    } catch (e) {
      print(e);
      Map<String, dynamic> error = Map<String, dynamic>();
      error["type"] = "error";
      print("$url -> {'type': 'error'}");
      return error;
    }
  }
}

class LightPower {
  double wattage;
  double wattHours;

  LightPower();

  static LightPower fromJSON(Map<String, dynamic> data) => LightPower()
    ..wattage = double.tryParse(data["w"]) ?? 0
    ..wattHours = double.tryParse(data["wh"]) ?? 0;
}

class GroupData {
  String name;
  String id;
  List<String> lights;
  double watts;
  double wattHours;

  GroupData();

  static List<String> convertToStringList(List<dynamic> l) {
    List<String> v = List<String>();

    for (int i = 0; i < l.length; i++) {
      v.add(l[i].toString());
    }

    return v;
  }

  static GroupData fromJSON(Map<String, dynamic> data) => GroupData()
    ..name = data["name"]
    ..id = data["id"]
    ..lights = convertToStringList(data["ids"])
    ..watts = double.tryParse(data["watts"].toString()) ?? 0
    ..wattHours = double.tryParse(data["wattHours"].toString()) ?? 0;
}

class LightData {
  String name;
  String id;
  int r;
  int g;
  int b;
  int a;
  double watts;
  double wattHours;

  LightData();

  Color getColor({bool alphaBrightness = false}) =>
      Color.fromARGB(alphaBrightness ? a : 255, r, g, b);

  static LightData fromJSON(Map<String, dynamic> data) => LightData()
    ..name = data["name"]
    ..id = data["id"]
    ..r = int.tryParse(data["r"].toString()) ?? 0
    ..g = int.tryParse(data["g"].toString()) ?? 0
    ..b = int.tryParse(data["b"].toString()) ?? 0
    ..a = int.tryParse(data["a"].toString()) ?? 0
    ..watts = double.tryParse(data["watts"].toString()) ?? 0
    ..wattHours = double.tryParse(data["wattHours"].toString()) ?? 0;
}
