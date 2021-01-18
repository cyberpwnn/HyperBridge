import 'dart:convert';

import 'package:dio/dio.dart';
import 'package:flutter/material.dart';

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

  static Future<bool> lockLight(String id) async =>
      (await get('$url/locklight?d={"id":"$id"}'))["type"] == "ok";

  static Future<bool> unlockLight(String id) async =>
      (await get('$url/unlocklight?d={"id":"$id"}'))["type"] == "ok";

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
    ..wattage = double.tryParse(data["w"].toString()) ?? 0
    ..wattHours = double.tryParse(data["wh"].toString()) ?? 0;
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
  int r = 0;
  int g = 0;
  int b = 0;
  int a = 0;
  double watts = 0;
  double wattHours = 0;
  bool locked = false;

  LightData();

  Color getColor({bool alphaBrightness = false}) =>
      Color.fromARGB(alphaBrightness ? a : 255, r, g, b);

  static LightData fromJSON(Map<String, dynamic> data) => LightData()
    ..name = data["name"]
    ..id = data["id"]
    ..locked = data["locked"]
    ..r = int.tryParse(data["r"].toString()) ?? 0
    ..g = int.tryParse(data["g"].toString()) ?? 0
    ..b = int.tryParse(data["b"].toString()) ?? 0
    ..a = int.tryParse(data["a"].toString()) ?? 0
    ..watts = double.tryParse(data["watts"].toString()) ?? 0
    ..wattHours = double.tryParse(data["wattHours"].toString()) ?? 0;
}
