<?xml version="1.0" encoding="UTF-8"?>
<project name="example" default="return_todo" basedir=".">
        <property name="src.dir" value="src"/>
        <property name="build.dir" value="build"/>

        <tstamp>
                <format property="current_for_version" pattern="HHmm"/>
        </tstamp>

        <target name="compile" depends="change_version">
                <mkdir dir="${build.dir}"/>
                <javac destdir="${build.dir}" >
                        <src path="${src.dir}" />
                </javac>
                <echo message="start compile "/>
        </target>

        <target name="change_version">
                <echo message="current time is ${current_for_version}"/>
                <echo message="change version "/>
                <replace file="${src.dir}/Main.java" token="0000" value="${current_for_version}"/>
        </target>

        <target name="return_todo" depends="compile">
                <replaceregexp file="${src.dir}/Main.java" match='version = ".*"' replace='version = "0000"' />
        </target>

</project>
