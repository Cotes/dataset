Author: Cristian Cotes

Parameters
----------

-r, --read-fs:
    If this parameter is not specified, the program will create a file system
    with the properties (tree levels, file sizes...) read from the config.properties
    file.
    If this parameter is specified, a file with a list of files (path+name) and
    sizes from the given path will be read for a future use.
    
-t/--test:
    This parameter could have the following values:
        update: This test creates only UPDATES actions. (modify files)
        generic: Test with all actions (ADD, UPDATE and REMOVE).

-g/--generate-dataset:
    Path to the file where we want to write the workload.

-e/--execute-wl
    Path to the file with the workload (actions) to be executed. this file is
    generated with the parameter -g.
    
Config parameters
-----------------
In the config.properties file there are some properties that could be changed:

    - AddPathFolder: Where to generate files before to execute the workload.
    - FolderPath: Folder where files will be copied when the workload is executed.
    
    - MaxWaitTime: Max. time between actions.
    - MinWaitTime: Min. time between actions.
    
    - TreeTotalData: Dataset total size.
    - TreeLevels: Number of levels of the file system.
    - TreeChilds: Number of folders inside a folder.
    
    - SizeMin: Minimum file size.
    - SizeMax: Maximum file size.
    
    - PercentageModified: Percentage of data modified by UPDATE actions.
    - ModificationSize: Bytes of data to modify in an UPDATE action.
    
    - NumOp: Number of actions to perform.
    

