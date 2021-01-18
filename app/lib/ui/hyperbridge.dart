import 'package:app/network.dart';
import 'package:app/ui/group.dart';
import 'package:app/ui/group_tile.dart';
import 'package:flutter/material.dart';

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

            return ListView(
              children: [
                Stack(
                  fit: StackFit.passthrough,
                  children: [
                    GridView.builder(
                      shrinkWrap: true,
                      physics: NeverScrollableScrollPhysics(),
                      padding: EdgeInsets.all(7),
                      itemCount: snap.data.length,
                      gridDelegate: SliverGridDelegateWithMaxCrossAxisExtent(
                          maxCrossAxisExtent: 200),
                      itemBuilder: (context, pos) => GroupTile(
                        group: snap.data[pos],
                        texts: false,
                      ),
                    ),
                    GridView.builder(
                      physics: NeverScrollableScrollPhysics(),
                      padding: EdgeInsets.all(7),
                      shrinkWrap: true,
                      itemCount: snap.data.length,
                      gridDelegate: SliverGridDelegateWithMaxCrossAxisExtent(
                          maxCrossAxisExtent: 200),
                      itemBuilder: (context, pos) => GroupTile(
                        onTap: () => Navigator.push(
                                context,
                                new MaterialPageRoute(
                                    builder: (context) =>
                                        EditGroup(snap.data[pos])))
                            .then((value) => setState(() {})),
                        group: snap.data[pos],
                      ),
                    )
                  ],
                )
              ],
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
    return FutureBuilder<LightPower>(
      future: Network.getPower(),
      builder: (context, snap) {
        if (!snap.hasData) {
          return Container();
        }

        return ListView(
          children: [
            Center(
              child: Container(
                child: Text(
                  "Hello",
                  style: TextStyle(fontSize: 42),
                ),
              ),
            ),
            ListTile(
              leading: Icon(Icons.power),
              title: Text(
                  "Using " + snap.data.wattage.toInt().toString() + " Watts"),
              subtitle:
                  Text("Used " + format(snap.data.wattHours) + " so far."),
            )
          ],
        );
      },
    );
  }

  Widget more(BuildContext context) {
    return Text("More");
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: index == 0
            ? lights(context)
            : index == 1
                ? haus(context)
                : more(context),
      ),
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

  String format(double wattHours) {
    if (wattHours < 1000) {
      return wattHours.toInt().toString() + " W";
    }

    if (wattHours / 1000 < 1000) {
      return (wattHours ~/ 1000).toString() + " kW";
    }

    return (wattHours / 1000 ~/ 1000).toString() + " gW";
  }
}
