// --- BEGIN COPYRIGHT BLOCK ---
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; version 2 of the License.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License along
// with this program; if not, write to the Free Software Foundation, Inc.,
// 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
//
// (C) 2012 Red Hat, Inc.
// All rights reserved.
// --- END COPYRIGHT BLOCK ---

package com.netscape.cmstools.group;

import java.util.Arrays;

import org.apache.commons.cli.CommandLine;

import com.netscape.certsrv.group.GroupMemberData;
import com.netscape.cmstools.cli.CLI;
import com.netscape.cmstools.cli.MainCLI;

/**
 * @author Endi S. Dewata
 */
public class GroupMemberShowCLI extends CLI {

    public GroupMemberCLI groupMemberCLI;

    public GroupMemberShowCLI(GroupMemberCLI groupMemberCLI) {
        super("show", "Show group member", groupMemberCLI);
        this.groupMemberCLI = groupMemberCLI;
    }

    public void printHelp() {
        formatter.printHelp(getFullName() + " <Group ID> <Member ID> [OPTIONS...]", options);
    }

    public void execute(String[] args) throws Exception {
        // Always check for "--help" prior to parsing
        if (Arrays.asList(args).contains("--help")) {
            // Display usage
            printHelp();
            System.exit(0);
        }

        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            printHelp();
            System.exit(-1);
        }

        String[] cmdArgs = cmd.getArgs();

        if (cmdArgs.length != 2) {
            System.err.println("Error: Incorrect number of arguments specified.");
            printHelp();
            System.exit(-1);
        }

        String groupID = args[0];
        String memberID = args[1];

        GroupMemberData groupMemberData = groupMemberCLI.groupClient.getGroupMember(groupID, memberID);

        MainCLI.printMessage("Group member \""+memberID+"\"");

        GroupMemberCLI.printGroupMember(groupMemberData);
    }
}
