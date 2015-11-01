# mds2mms

Convert My Digital Studio projects to My Memory Suite albums.

## Overview

mds2mms is a command line tool that will modify your My Digital Studio (MDS) project in place to make it compatible with My Memory Suite (MMS).

## Usage

```
Usage: java -jar mds2mms.jar project_directory
```

## Details

My Digital Studio is essentially a white-labeled version of My Memory Suite produced by [StoryRock](http://www.storyrock.com/) for [Stampin' Up!](http://www.stampinup.com/). Unfortunately, Stampin' Up! discontinued MDS as of May 15, 2015. To continue to print their scrap books, MDS users must migrate to MMS 5, which has thankfully been offered as a free download. The [migration instructions](https://www.mymemories.com/store/my_digital_studio) are relatively straightforward and have been provided by [MyMemories](http://www.mymemories.com) (StoryRock).

After doing the migration, we discovered that most of the projects had issues with text size either being too large or too small. We contacted MyMemories support and they replied with the following:

> I just spoke with our developers and they said that unfortunately there are not any settings that could be applied manually to change the text scaling to match MDS.  The would need to change it at the program level for it to display the same.  
>
> Stampin UP had asked our developers to scale the text smaller for MDS than what we did with MMS.  So unfortunately the scaling is going to be different between the programs.  They said you can find scaling differences between other programs as well and with MMS they just scaled the text up.  So while using MMS you will need to use a smaller font size to match the size of the MDS text.
>
> Unfortunately we don’t have a work around other than selecting a smaller text size in MMS. I apologize for the inconvenience.

This is unacceptable, especially if you have years worth of projects with many pages. We reached out for additional technical information, and they shared that the scaling factor is the ratio of 8 to the project height. So, if you have a 12x12 project, the scaling factor would be 0.6667 or if you have a 4x6 project, the scaling factor would be 2.

So, mds2mms takes care of scaling all of the text objects, so you don't have to do it manually. It also takes care of changing the extension to mms as described in Step 3 of the [migration instructions](https://www.mymemories.com/store/my_digital_studio).
