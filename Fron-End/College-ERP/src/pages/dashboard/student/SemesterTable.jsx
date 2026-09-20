import { useEffect, useState } from "react";
import axios from "axios";
import { Card, CardBody, Typography, Chip } from "@material-tailwind/react";

const API = "http://localhost:8080/api";

export function SemesterTable() {
  const [semesters, setSemesters] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const studentId = localStorage.getItem("studentId");
    if (!studentId) {
      setError("No student session found. Please sign in again.");
      setLoading(false);
      return;
    }
    axios.get(`${API}/semesters/student/${studentId}`)
      .then(({ data }) => setSemesters(data || []))
      .catch((e) => setError(e.response?.data || "Unable to load your semester records."))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="mt-10">Loading academic records...</div>;

  return (
    <div className="mt-8 mb-8 space-y-6">
      <div>
        <Typography variant="h4" color="blue-gray">My Semester Records</Typography>
        <Typography className="mt-1" color="blue-gray">
          Subjects and marks shown here are created and maintained by the HOD from the academic records module.
        </Typography>
      </div>
      {error && <div className="rounded-lg bg-red-50 p-3 text-sm text-red-700">{error}</div>}
      {!error && semesters.length === 0 && (
        <Card><CardBody><Typography color="blue-gray">No semester records have been assigned to you yet. Please contact your HOD.</Typography></CardBody></Card>
      )}
      {semesters.map((semester) => (
        <Card key={semester.id}>
          <CardBody>
            <div className="mb-5 flex flex-wrap items-start justify-between gap-3">
              <div>
                <Typography variant="h5">{semester.semester}</Typography>
                <Typography variant="small" color="blue-gray">
                  {semester.course?.code || "Course"} — {semester.course?.name || "Not assigned"}
                  {semester.course?.professor?.name ? ` • ${semester.course.professor.name}` : ""}
                </Typography>
              </div>
              <Chip value={`${semester.subjects?.length || 0} subjects`} variant="outlined" />
            </div>
            <div className="overflow-x-auto">
              <table className="w-full min-w-[760px] table-auto text-left">
                <thead><tr>{["Code", "Subject", "Credits", "CT-1", "CT-2", "Theory", "Total", "Grade"].map((h) => <th key={h} className="border-b p-3 text-xs font-bold uppercase text-blue-gray-400">{h}</th>)}</tr></thead>
                <tbody>
                  {(semester.subjects || []).map((subject) => {
                    const total = Number(subject.ct1 || 0) + Number(subject.ct2 || 0) + Number(subject.theory || 0);
                    return <tr key={subject.id || subject.code} className="border-b last:border-b-0">
                      <td className="p-3 font-semibold">{subject.code || "-"}</td>
                      <td className="p-3">{subject.name || "-"}</td>
                      <td className="p-3">{subject.credits}</td>
                      <td className="p-3">{subject.ct1}</td><td className="p-3">{subject.ct2}</td><td className="p-3">{subject.theory}</td>
                      <td className="p-3 font-semibold">{total}</td>
                      <td className="p-3">{subject.grade || "Not graded"}</td>
                    </tr>;
                  })}
                </tbody>
              </table>
            </div>
            {semester.practicals?.length > 0 && <div className="mt-6 overflow-x-auto">
              <Typography variant="h6" className="mb-3">Practical Records</Typography>
              <table className="w-full min-w-[600px] table-auto text-left"><thead><tr>{["Practical", "Written", "Viva", "Total", "Grade"].map(h=><th key={h} className="border-b p-3 text-xs font-bold uppercase text-blue-gray-400">{h}</th>)}</tr></thead><tbody>
                {semester.practicals.map((p) => <tr key={p.id || p.name} className="border-b last:border-b-0"><td className="p-3">{p.name}</td><td className="p-3">{p.written}</td><td className="p-3">{p.viva}</td><td className="p-3">{Number(p.written||0)+Number(p.viva||0)}</td><td className="p-3">{p.grade || "Not graded"}</td></tr>)}
              </tbody></table>
            </div>}
          </CardBody>
        </Card>
      ))}
    </div>
  );
}

export default SemesterTable;
