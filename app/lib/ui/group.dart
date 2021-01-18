import 'package:app/network.dart';
import 'package:app/ui/light_picker.dart';
import 'package:app/ui/light_tile.dart';
import 'package:flutter/material.dart';

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
        List<String> lights = List<String>();
        snap.data.lights.forEach((element) {
          lights.add(element);
        });
        return Scaffold(
          appBar: AppBar(
            actions: [
              IconButton(
                icon: Icon(Icons.add),
                tooltip: "Add Light",
                onPressed: () => Navigator.push(
                    context,
                    MaterialPageRoute(
                        builder: (context) => LightPicker(
                              checked: snap.data.lights,
                            ))).then((value) {
                  if (value != null) {
                    List<Future<bool>> f = List<Future<bool>>();

                    for (int i = 0; i < lights.length; i++) {
                      String rem = lights[i];

                      if (!value.contains(rem)) {
                        f.add(Network.removeLightFromGroup(rem, widget.id));
                      }
                    }

                    for (int i = 0; i < value.length; i++) {
                      String add = value[i];

                      if (!lights.contains(add)) {
                        f.add(Network.addLightToGroup(add, widget.id));
                      }
                    }

                    Future.wait(f).then((value) => setState(() {}));
                  }
                }),
              ),
              IconButton(
                icon: Icon(Icons.delete),
                onPressed: () {
                  Widget cancelButton = FlatButton(
                    child: Text("Cancel"),
                    onPressed: () {
                      Navigator.pop(context);
                    },
                  );
                  Widget continueButton = FlatButton(
                    child: Text("Delete Group"),
                    onPressed: () {
                      Network.removeGroup(widget.id).then((value) {
                        Navigator.pop(context);
                        Navigator.pop(context);
                      });
                    },
                  );

                  // set up the AlertDialog
                  AlertDialog alert = AlertDialog(
                    title: Text("Delete Group?"),
                    content:
                        Text("Are you sure you want to delete this group?"),
                    actions: [
                      cancelButton,
                      continueButton,
                    ],
                  );
                  showDialog(
                    context: context,
                    builder: (BuildContext context) {
                      return alert;
                    },
                  );
                },
              )
            ],
            title: TextField(
              controller: tc,
              onSubmitted: (v) => Network.setGroupName(widget.id, v),
              onChanged: (v) => Network.setGroupName(widget.id, v),
            ),
          ),
          body: ListView(
            children: [
              GridView.builder(
                itemCount: snap.data.lights.length,
                padding: EdgeInsets.all(7),
                gridDelegate: SliverGridDelegateWithMaxCrossAxisExtent(
                    maxCrossAxisExtent: 200),
                itemBuilder: (context, pos) => InkWell(
                  onTap: () {},
                  child: LightTile(
                    light: snap.data.lights[pos],
                  ),
                ),
                shrinkWrap: true,
              )
            ],
          ),
        );
      },
    );
  }
}
