package com.example.gitnb.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable.Creator;

/**
 * Created by Bernat on 20/07/2014.
 */
public class Content extends ShaUrl implements Comparable<Content> {
	public ContentType type;
	public int size;
	public String name;
	public String content;
	public String path;
	public String git_url;
	public Links _links;
	public String encoding;
	public List<Content> children;
	public Content parent;
	
    public Content() {

    }
    
    protected Content(Parcel in) {
    	super(in);
    	type = in.readParcelable(ContentType.class.getClassLoader());
    	size = in.readInt();
    	name = in.readString();
    	content = in.readString();
    	path = in.readString();
    	git_url = in.readString();
    	_links = in.readParcelable(Links.class.getClassLoader());
    	encoding = in.readString();
    	children = new ArrayList<Content>(); 
    	in.readList(children, Content.class.getClassLoader());
    	content = in.readParcelable(Content.class.getClassLoader());
    }

    public static final Creator<Content> CREATOR = new Creator<Content>() {
        @Override
        public Content createFromParcel(Parcel in) {
            return new Content(in);
        }

        @Override
        public Content[] newArray(int size) {
            return new Content[size];
        }
    };
    
	public boolean isDir() {
		return ContentType.dir.equals(type);
	}

	public boolean isFile() {
		return ContentType.file.equals(type);
	}

	public boolean isSubmodule() {
		return ContentType.submodule.equals(type);
	}


	@Override
	public int compareTo(Content another) {
		return Comparators.TYPE.compare(this, another);
	}


	public static class Comparators {
		public static Comparator<Content> TYPE = new Comparator<Content>() {
			@Override
			public int compare(Content content, Content content2) {
				if (content.type == ContentType.dir) {
					if (content2.type == ContentType.dir) {
						return content.name.compareTo(content2.name);
					} else {
						return -1;
					}
				} else if (content.type == ContentType.submodule) {
					if (content2.type == ContentType.submodule) {
						return 1;
					} else {
						return -1;
					}
				}
				return content.name.compareTo(content2.name);
			}
		};
	}    
	
	@Override
    public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
        dest.writeParcelable(type, flags);
		dest.writeInt(size);
		dest.writeString(name);
		dest.writeString(content);
		dest.writeString(path);
		dest.writeString(git_url);
        dest.writeParcelable(_links, flags);
		dest.writeString(encoding);
		dest.writeList(children);
        dest.writeParcelable(parent, flags);
    }
}
