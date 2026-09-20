import { useEffect, useState } from "react";
import axios from "axios";
import { Card, CardBody, Typography } from "@material-tailwind/react";

export function SemesterTable() {
  const [courses, setCourses] = useState([]); const [error, setError] = useState("");
  useEffect(() => {
    const id = localStorage.getItem("professorId");
    if (!id) { setError("No professor session found. Please sign in again."); return; }
    Promise.all([axios.get(`http://localhost:8080/api/professors/${id}`), axios.get("http://localhost:8080/api/courses")])
      .then(([p, c]) => setCourses((c.data || []).filter(course => course.professor?.id === p.data.id)))
      .catch(e => setError(e.response?.data || "Unable to load assigned courses."));
  }, []);
  return <div className="mt-8 mb-8 space-y-6"><div><Typography variant="h4">My Assigned Courses</Typography><Typography color="blue-gray">Courses assigned to the signed-in professor by the HOD.</Typography></div>{error && <div className="rounded bg-red-50 p-3 text-red-700">{error}</div>}{!error && courses.length === 0 && <Card><CardBody><Typography color="blue-gray">No courses have been assigned to you yet.</Typography></CardBody></Card>}{courses.map(c=><Card key={c.id}><CardBody><Typography variant="h5">{c.code} — {c.name}</Typography><Typography color="blue-gray">Credits: {c.credits}</Typography></CardBody></Card>)}</div>;
}
export default SemesterTable;
