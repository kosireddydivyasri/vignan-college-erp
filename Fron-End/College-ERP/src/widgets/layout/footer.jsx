import PropTypes from "prop-types";
import { Typography } from "@material-tailwind/react";

export function Footer() {
  return (
    <footer className="py-2 text-center">
      <Typography variant="small" className="font-normal text-inherit">
        Vignan College ERP &middot; Academic Management System
      </Typography>
    </footer>
  );
}

Footer.propTypes = {};
Footer.displayName = "/src/widgets/layout/footer.jsx";
export default Footer;
