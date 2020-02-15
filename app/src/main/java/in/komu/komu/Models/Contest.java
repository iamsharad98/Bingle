package in.komu.komu.Models;

import java.util.List;

public class Contest {
//    private Video video;
    private List<ContestDescription> contestDescription;

    public Contest() {
    }

    public Contest(List<ContestDescription> contestDescription) {
        this.contestDescription = contestDescription;
    }

    public List<ContestDescription> getContestDescription() {
        return contestDescription;
    }

    public void setContestDescription(List<ContestDescription> contestDescription) {
        this.contestDescription = contestDescription;
    }

    @Override
    public String toString() {
        return "Contest{" +
                "contestDescription=" + contestDescription +
                '}';
    }
}
