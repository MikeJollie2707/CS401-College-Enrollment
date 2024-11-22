# ...

- `ClientMain.java`
    - Run this to run client.
- `MainFrame.java`
    - Responsible for the `JFrame` used.
- `GUI...java`
    - `JPanel` used **directly** by `MainFrame`.
    - `GUIAdmin` is the GUI for Administrator, `GUIStudent` is the GUI for Student, and so on.
    - The only exception is `GUI.java`, which is deprecated.
    - Aside from `GUI` and `GUILogin`, the rest will contain `panelMap` attribute.
- `Panel...java`
    - `JPanel` used **directly** by `GUI...`. They are **NOT** meant to be used by `MainFrame` directly.
    - These will mostly correspond to each buttons on the sidebar of each GUI.
        - Example: `PanelCreateCourse` is the `JPanel` to render when the user click on a button like "Create Course", `PanelCatalog` is the `JPanel` to render when the user click on a button like "Search Course".
    - Must extends `PanelBase.java`.
- `Component...java`
    - Meant to render objects in `objects`. For example, `ComponentCourse` will return a `JPanel` that render a `Course` (assuming it has complete information).
    - This allows objects to be rendered uniformly.
- Other classes
    - Util classes.