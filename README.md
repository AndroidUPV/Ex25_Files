# Ex25_Files

Lecture 06 - Local Storage

The app displays a dropdown menu to select different options for storage space.
An EditText will display the content of text files and will enable the edition of their content whenever possible.
The Save Button will update the content of the selected file.
For PNG images files, a RecyclerView will display a thumbnail and name of the images.
The Save Button will the create a new PNG image file.

All permissions are checked and requested in runtime.

Available options are:
- Resource file: Not editable text file.
- Asset file: Not editable text file.
- Private internal storage: Editable text file.
- Private internal cached storage: Editable text file.
- Private external storage: Editable text file.
- Private external cached storage: Editable text file.
- Private external images storage: Lists all PNG files. Creates a new PNG image file.
- Public media storage:  Lists all PNG files. Creates a new PNG image file.
- Public other storage: Editable text file. Creates a new texr file. 